package dev.olog.shared_android

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dev.olog.shared_android.interfaces.MusicServiceClass

object PendingIntents {

    private const val TAG = "PendingIntents"
    const val ACTION_STOP_SLEEP_END = TAG + ".action.stop_sleep_timer"

    fun stopServiceIntent(context: Context, serviceClass: MusicServiceClass): PendingIntent {
        val intent = Intent(context, serviceClass.get())
        intent.action = ACTION_STOP_SLEEP_END
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
    }

    fun stopServiceIntent(context: Context, serviceClass: Class<*>): PendingIntent {
        val intent = Intent(context, serviceClass)
        intent.action = ACTION_STOP_SLEEP_END
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
    }


}