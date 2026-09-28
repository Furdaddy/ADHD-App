package app.nextstep

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/** Rings when a focus session ends, even if the app is closed or the screen is off. */
object FocusTimer {
    private const val CHANNEL_RUNNING = "focus_running"
    private const val CHANNEL_DONE = "focus_done"
    private const val NOTIFICATION_ID = 1001
    const val EXTRA_MINS = "mins"
    const val EXTRA_LABEL = "label"

    fun start(context: Context, endAt: Long, mins: Int, label: String) {
        ensureChannels(context)
        val alarms = context.getSystemService(AlarmManager::class.java)
        val pending = alarmIntent(context, mins, label)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarms.canScheduleExactAlarms()) {
            alarms.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, endAt, pending)
        } else {
            alarms.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, endAt, pending)
        }

        val running = NotificationCompat.Builder(context, CHANNEL_RUNNING)
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle(label.ifBlank { context.getString(R.string.focus_running) })
            .setContentText(context.getString(R.string.focus_running_text, mins))
            .setUsesChronometer(true)
            .setChronometerCountDown(true)
            .setWhen(endAt)
            .setShowWhen(true)
            .setOngoing(true)
            .setSilent(true)
            .setTimeoutAfter((endAt - System.currentTimeMillis()).coerceAtLeast(1000))
            .setContentIntent(MainActivity.openIntent(context, "focus", 20))
            .build()
        notify(context, running)
    }

    fun cancel(context: Context) {
        context.getSystemService(AlarmManager::class.java).cancel(alarmIntent(context, 0, ""))
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
    }

    fun finish(context: Context, mins: Int, label: String) {
        ensureChannels(context)
        val done = NotificationCompat.Builder(context, CHANNEL_DONE)
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle(context.getString(R.string.focus_done_title, mins))
            .setContentText(
                if (label.isBlank()) context.getString(R.string.focus_done_text)
                else context.getString(R.string.focus_done_text_label, label),
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(MainActivity.openIntent(context, "focus", 21))
            .build()
        notify(context, done)
    }

    @SuppressLint("MissingPermission") // checked via areNotificationsEnabled()
    private fun notify(context: Context, notification: android.app.Notification) {
        val manager = NotificationManagerCompat.from(context)
        if (!manager.areNotificationsEnabled()) return
        try {
            manager.notify(NOTIFICATION_ID, notification)
        } catch (_: SecurityException) {
            // Notification permission was revoked; the in-app timer still works.
        }
    }

    private fun alarmIntent(context: Context, mins: Int, label: String): PendingIntent =
        PendingIntent.getBroadcast(
            context, 30,
            Intent(context, TimerAlarmReceiver::class.java)
                .putExtra(EXTRA_MINS, mins)
                .putExtra(EXTRA_LABEL, label),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )

    private fun ensureChannels(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_RUNNING, context.getString(R.string.channel_running), NotificationManager.IMPORTANCE_LOW),
        )
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_DONE, context.getString(R.string.channel_done), NotificationManager.IMPORTANCE_HIGH)
                .apply { enableVibration(true) },
        )
    }
}
