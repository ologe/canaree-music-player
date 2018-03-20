package dev.olog.msc.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dev.olog.msc.music.service.MusicService

object PendingIntents {

    private const val TAG = "PendingIntents"
    const val ACTION_STOP_SLEEP_END = "$TAG.action.stop_sleep_timer"

    fun stopMusicServiceIntent(context: Context): PendingIntent {
        val intent = Intent(context, MusicService::class.java)
        intent.action = ACTION_STOP_SLEEP_END
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
    }

}