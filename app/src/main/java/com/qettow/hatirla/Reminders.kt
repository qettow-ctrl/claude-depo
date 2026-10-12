package com.qettow.hatirla

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

/** Her notu kilit ekranında görünen kalıcı bir bildirim olarak gösterir ve widget'ı tazeler. */
object Reminders {
    private const val CHANNEL_ID = "notes"

    fun refresh(context: Context) {
        showNotifications(context)
        updateWidgets(context)
    }

    private fun showNotifications(context: Context) {
        val manager = NotificationManagerCompat.from(context)
        createChannel(context)

        val notes = NoteStore.all(context)
        val activeIds = manager.activeNotifications.map { it.id }.toSet()
        activeIds.filter { id -> notes.none { it.id == id } }.forEach { manager.cancel(it) }

        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return

        notes.forEach { note ->
            val openApp = PendingIntent.getActivity(
                context, note.id,
                Intent(context, NoteEditActivity::class.java).putExtra(NoteEditActivity.EXTRA_ID, note.id),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val done = PendingIntent.getBroadcast(
                context, note.id,
                Intent(context, DoneReceiver::class.java).putExtra(DoneReceiver.EXTRA_ID, note.id),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_note)
                .setContentTitle(note.text.lineSequence().first())
                .setContentText(note.text)
                .setStyle(NotificationCompat.BigTextStyle().bigText(note.text))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .setSilent(true)
                .setShowWhen(false)
                .setGroup("notes_${note.id}") // gruplanıp gizlenmesin, her not ayrı görünsün
                .setContentIntent(openApp)
                .addAction(R.drawable.ic_check, context.getString(R.string.done), done)
                .build()
            manager.notify(note.id, notification)
        }
    }

    private fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.channel_description)
            lockscreenVisibility = android.app.Notification.VISIBILITY_PUBLIC
            setSound(null, null)
            enableVibration(false)
            setShowBadge(true)
        }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun updateWidgets(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(ComponentName(context, NotesWidget::class.java))
        if (ids.isEmpty()) return
        context.sendBroadcast(
            Intent(context, NotesWidget::class.java)
                .setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        )
    }
}
