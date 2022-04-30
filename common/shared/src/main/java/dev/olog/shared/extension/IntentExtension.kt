package dev.olog.shared.extension

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dev.olog.shared.isMarshmallow
import dev.olog.shared.isOreo

fun Intent.asServicePendingIntent(context: Context, flag: Int = PendingIntent.FLAG_CANCEL_CURRENT): PendingIntent{
    var flags = flag
    if (isMarshmallow()) {
        flags = flags or PendingIntent.FLAG_IMMUTABLE
    }
    if (isOreo()){
        return PendingIntent.getForegroundService(context, 0, this, flags)
    }
    return PendingIntent.getService(context, 0, this, flags)
}

fun Intent.asActivityPendingIntent(context: Context, flag: Int = PendingIntent.FLAG_CANCEL_CURRENT): PendingIntent{
    var flags = flag
    if (isMarshmallow()) {
        flags = flags or PendingIntent.FLAG_IMMUTABLE
    }
    return PendingIntent.getActivity(context, 0, this, flags)
}