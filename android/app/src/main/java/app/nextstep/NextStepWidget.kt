package app.nextstep

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews

class NextStepWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        manager.updateAppWidget(ids, build(context))
    }

    companion object {
        fun refresh(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, NextStepWidget::class.java))
            if (ids.isNotEmpty()) manager.updateAppWidget(ids, build(context))
        }

        private fun build(context: Context): RemoteViews {
            val s = StateStore.summary(context)
            val views = RemoteViews(context.packageName, R.layout.widget_next_step)
            when {
                s.task == null -> {
                    views.setTextViewText(R.id.step, context.getString(R.string.widget_empty))
                    views.setTextViewText(R.id.from, context.getString(R.string.widget_empty_hint))
                }
                s.step != null -> {
                    views.setTextViewText(R.id.step, s.step)
                    views.setTextViewText(R.id.from, context.getString(R.string.widget_part_of, s.task))
                }
                else -> {
                    views.setTextViewText(R.id.step, s.task)
                    views.setTextViewText(R.id.from, context.getString(R.string.widget_left, s.left))
                }
            }
            views.setViewVisibility(R.id.done, if (s.task != null) View.VISIBLE else View.GONE)
            views.setViewVisibility(R.id.focus, if (s.task != null) View.VISIBLE else View.GONE)
            views.setTextViewText(
                R.id.wins,
                context.resources.getQuantityString(R.plurals.wins_today, s.wins, s.wins),
            )

            views.setOnClickPendingIntent(R.id.body, MainActivity.openIntent(context, "today", 10))
            views.setOnClickPendingIntent(R.id.focus, MainActivity.openIntent(context, "start-focus", 11))
            views.setOnClickPendingIntent(R.id.dump, MainActivity.openIntent(context, "dump", 12))
            views.setOnClickPendingIntent(
                R.id.done,
                PendingIntent.getBroadcast(
                    context, 13,
                    Intent(context, WidgetActionReceiver::class.java).setAction(WidgetActionReceiver.ACTION_DONE),
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
                ),
            )
            return views
        }
    }
}
