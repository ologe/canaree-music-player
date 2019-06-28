package dev.olog.shared

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dev.olog.shared.extensions.asServicePendingIntent

object PendingIntents {

    private const val TAG = "PendingIntents"
    const val ACTION_STOP_SLEEP_END = "$TAG.action.stop_sleep_timer"

    fun stopMusicServiceIntent(context: Context): PendingIntent {
        val intent = Intent(context, Class.forName(Classes.SERVICE_MUSIC))
        intent.action = ACTION_STOP_SLEEP_END
        return intent.asServicePendingIntent(context, PendingIntent.FLAG_CANCEL_CURRENT)
    }

}