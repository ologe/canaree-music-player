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

    fun service(
        intent: Intent,
        requestCode: Int = 0,
        flag: Int = PendingIntent.FLAG_CANCEL_CURRENT,
    ): PendingIntent {
        var adjustedFlags = flag
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            adjustedFlags = flag or PendingIntent.FLAG_IMMUTABLE
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return PendingIntent.getForegroundService(context, requestCode, intent, adjustedFlags)
        }
        return PendingIntent.getService(context, requestCode, intent, adjustedFlags)
    }

    fun activity(
        intent: Intent,
        requestCode: Int = 0,
        flag: Int = PendingIntent.FLAG_CANCEL_CURRENT,
    ): PendingIntent {
        var adjustedFlags = flag
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            adjustedFlags = flag or PendingIntent.FLAG_IMMUTABLE
        }

        return PendingIntent.getActivity(context, requestCode, intent, adjustedFlags)
    }

}