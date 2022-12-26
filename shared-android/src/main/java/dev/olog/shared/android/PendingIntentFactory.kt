package dev.olog.shared.android

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.shared.android.utils.isMarshmallow
import dev.olog.shared.android.utils.isOreo
import javax.inject.Inject

class PendingIntentFactory @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun createForService(
        intent: Intent,
        flag: Int = PendingIntent.FLAG_CANCEL_CURRENT,
    ): PendingIntent {
        var flags = flag
        if (isMarshmallow()) {
            flags = flags or PendingIntent.FLAG_IMMUTABLE
        }

        if (isOreo()){
            return PendingIntent.getForegroundService(context, 0, intent, flags)
        }
        return PendingIntent.getService(context, 0, intent, flags)
    }

    fun createForActivity(
        intent: Intent,
        flag: Int = PendingIntent.FLAG_CANCEL_CURRENT,
    ): PendingIntent {
        var flags = flag
        if (isMarshmallow()) {
            flags = flags or PendingIntent.FLAG_IMMUTABLE
        }
        return PendingIntent.getActivity(context, 0, intent, flags)
    }

}