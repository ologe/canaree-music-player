package dev.olog.feature.media.interfaces

import android.app.Notification
import dev.olog.feature.media.model.MusicNotificationState

internal interface INotification {

    companion object {
        const val NOTIFICATION_ID: Int = 0x6d7363
        const val CHANNEL_ID = "$NOTIFICATION_ID"
        const val IMAGE_SIZE = 200
    }

    suspend fun update(state: MusicNotificationState): Notification
    fun cancel()

}