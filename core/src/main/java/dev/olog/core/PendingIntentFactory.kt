package dev.olog.core

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PendingIntentFactory @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun createForService(
        intent: Intent,
        flag: Int = PendingIntent.FLAG_CANCEL_CURRENT
    ): PendingIntent {
        var flags = flag
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags = flags or PendingIntent.FLAG_IMMUTABLE
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return PendingIntent.getForegroundService(context, 0, intent, flags)
        }
        return PendingIntent.getService(context, 0, intent, flags)
    }

    fun createForActivity(
        intent: Intent,
        flag: Int = PendingIntent.FLAG_CANCEL_CURRENT,
    ): PendingIntent {
        var flags = flag
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags = flags or PendingIntent.FLAG_IMMUTABLE
        }
        return PendingIntent.getActivity(context, 0, intent, flags)
    }

}