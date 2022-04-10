@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.shared.android.extensions

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import dev.olog.shared.android.utils.isOreo

inline fun Intent.asServicePendingIntent(context: Context, flag: Int = PendingIntent.FLAG_CANCEL_CURRENT): PendingIntent{
    var flags = flag
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        flags = flags or PendingIntent.FLAG_IMMUTABLE
    }
    if (isOreo()){
        return PendingIntent.getForegroundService(context, 0, this, flags)
    }
    return PendingIntent.getService(context, 0, this, flags)
}

inline fun Intent.asActivityPendingIntent(context: Context, flag: Int = PendingIntent.FLAG_CANCEL_CURRENT): PendingIntent{
    var flags = flag
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        flags = flags or PendingIntent.FLAG_IMMUTABLE
    }
    return PendingIntent.getActivity(context, 0, this, flags)
}