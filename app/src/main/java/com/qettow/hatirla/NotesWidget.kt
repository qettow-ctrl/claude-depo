package com.qettow.hatirla

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import android.widget.RemoteViewsService

/** Ana ekran widget'ı: notların listesi ve "+" düğmesi. */
class NotesWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { id ->
            val views = RemoteViews(context.packageName, R.layout.widget_notes)

            val service = Intent(context, NotesWidgetService::class.java)
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
            service.data = Uri.parse(service.toUri(Intent.URI_INTENT_SCHEME))
            views.setRemoteAdapter(R.id.widget_list, service)
            views.setEmptyView(R.id.widget_list, R.id.widget_empty)

            val add = PendingIntent.getActivity(
                context, 0, Intent(context, NoteEditActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_add, add)

            val open = PendingIntent.getActivity(
                context, 1, Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_title, open)
            views.setOnClickPendingIntent(R.id.widget_empty, open)

            // Liste öğelerine dokununca o not düzenlenir (öğe kendi id'sini fillInIntent ile ekler).
            val edit = PendingIntent.getActivity(
                context, 2, Intent(context, NoteEditActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
            views.setPendingIntentTemplate(R.id.widget_list, edit)

            manager.updateAppWidget(id, views)
            manager.notifyAppWidgetViewDataChanged(id, R.id.widget_list)
        }
    }
}

class NotesWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory = Factory(applicationContext)

    private class Factory(private val context: Context) : RemoteViewsFactory {
        private var notes: List<Note> = emptyList()

        override fun onCreate() {}
        override fun onDataSetChanged() {
            notes = NoteStore.all(context)
        }
        override fun onDestroy() {}
        override fun getCount() = notes.size
        override fun getViewAt(position: Int): RemoteViews {
            val note = notes[position]
            return RemoteViews(context.packageName, R.layout.widget_item).apply {
                setTextViewText(R.id.widget_item_text, note.text)
                setOnClickFillInIntent(
                    R.id.widget_item_text,
                    Intent().putExtra(NoteEditActivity.EXTRA_ID, note.id)
                )
            }
        }
        override fun getLoadingView(): RemoteViews? = null
        override fun getViewTypeCount() = 1
        override fun getItemId(position: Int) = notes[position].id.toLong()
        override fun hasStableIds() = true
    }
}
