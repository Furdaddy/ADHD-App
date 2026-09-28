package app.nextstep

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * The app's state is one JSON document owned by the web UI. It is mirrored here so the
 * widget can show the next step and mark it done while the app is closed.
 */
object StateStore {
    private const val PREFS = "nextstep"
    private const val KEY_STATE = "state"

    data class Summary(val step: String?, val task: String?, val left: Int, val wins: Int)

    fun read(context: Context): String? =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_STATE, null)

    fun write(context: Context, json: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY_STATE, json).apply()
    }

    fun dayKey(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    private fun parse(context: Context): JSONObject? =
        read(context)?.let { runCatching { JSONObject(it) }.getOrNull() }

    private fun openTodayTasks(state: JSONObject): List<JSONObject> {
        val tasks = state.optJSONArray("tasks") ?: JSONArray()
        return (0 until tasks.length())
            .mapNotNull { tasks.optJSONObject(it) }
            .filter { it.optBoolean("today") && !it.optBoolean("done") }
    }

    private fun firstOpenStep(task: JSONObject): JSONObject? {
        val steps = task.optJSONArray("steps") ?: return null
        return (0 until steps.length()).mapNotNull { steps.optJSONObject(it) }.firstOrNull { !it.optBoolean("done") }
    }

    fun summary(context: Context): Summary {
        val state = parse(context) ?: return Summary(null, null, 0, 0)
        val open = openTodayTasks(state)
        val first = open.firstOrNull()
        val wins = state.optJSONObject("wins")?.optInt(dayKey(), 0) ?: 0
        return Summary(first?.let { firstOpenStep(it)?.optString("text") }, first?.optString("title"), open.size, wins)
    }

    /** Marks the current next step (or the whole task, if it has no steps) done. Returns a short message. */
    fun completeNext(context: Context): String? {
        val state = parse(context) ?: return null
        val task = openTodayTasks(state).firstOrNull() ?: return null
        val step = firstOpenStep(task)
        val now = System.currentTimeMillis()
        if (step != null) step.put("done", true)
        val steps = task.optJSONArray("steps")
        val allDone = steps == null || (0 until steps.length()).all { steps.optJSONObject(it)?.optBoolean("done") != false }
        if (step == null || allDone) {
            task.put("done", true)
            task.put("doneAt", now)
            if (steps != null) for (i in 0 until steps.length()) steps.optJSONObject(i)?.put("done", true)
        }
        val wins = state.optJSONObject("wins") ?: JSONObject().also { state.put("wins", it) }
        val today = wins.optInt(dayKey(), 0) + 1
        wins.put(dayKey(), today)
        state.put("updatedAt", now)
        write(context, state.toString())
        return "Done. $today win${if (today == 1) "" else "s"} today."
    }
}
