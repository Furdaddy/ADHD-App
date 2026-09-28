package app.nextstep

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class WidgetActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_DONE) return
        val message = StateStore.completeNext(context)
        NextStepWidget.refresh(context)
        if (message != null) Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val ACTION_DONE = "app.nextstep.action.DONE"
    }
}
