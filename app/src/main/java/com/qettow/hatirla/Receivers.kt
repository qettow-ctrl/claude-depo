package com.qettow.hatirla

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Bildirimdeki "Tamam" düğmesi: notu siler. */
class DoneReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getIntExtra(EXTRA_ID, -1)
        if (id >= 0) NoteStore.delete(context, id)
    }

    companion object {
        const val EXTRA_ID = "id"
    }
}

/** Telefon yeniden başladığında ya da uygulama güncellendiğinde bildirimleri geri getirir. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED
        ) {
            Reminders.refresh(context)
        }
    }
}
