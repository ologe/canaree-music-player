package dev.olog.shared.android

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

object PendingIntentFactory {

    fun ofActivity(
        context: Context,
        intent: Intent,
        requestCode: Int = 0,
        flag: Int = PendingIntent.FLAG_CANCEL_CURRENT
    ) : PendingIntent {
        return PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            flag.fixFlags()
        )
    }

    fun ofForegroundService(
        context: Context,
        intent: Intent,
        requestCode: Int = 0,
        flag: Int = PendingIntent.FLAG_CANCEL_CURRENT
    ): PendingIntent {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            return PendingIntent.getForegroundService(
                context,
                requestCode,
                intent,
                flag.fixFlags()
            )
        }
        return PendingIntent.getService(
            context,
            requestCode,
            intent,
            flag.fixFlags()
        )
    }

    private fun Int.fixFlags(): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return this or PendingIntent.FLAG_IMMUTABLE
        }
        return this
    }

}