@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.shared.android.extensions

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dev.olog.core.isOreo

inline fun Intent.asServicePendingIntent(context: Context, flag: Int = PendingIntent.FLAG_CANCEL_CURRENT): PendingIntent{
    if (isOreo()){
        return PendingIntent.getForegroundService(context, 0, this, flag)
    }
    return PendingIntent.getService(context, 0, this, flag)
}

inline fun Intent.asActivityPendingIntent(context: Context, flag: Int = PendingIntent.FLAG_CANCEL_CURRENT): PendingIntent{
    return PendingIntent.getActivity(context, 0, this, flag)
}