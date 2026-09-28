package app.nextstep

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TimerAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        FocusTimer.finish(
            context,
            intent.getIntExtra(FocusTimer.EXTRA_MINS, 0),
            intent.getStringExtra(FocusTimer.EXTRA_LABEL).orEmpty(),
        )
    }
}
