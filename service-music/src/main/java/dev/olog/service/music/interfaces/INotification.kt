package dev.olog.service.music.interfaces

import android.app.Notification
import dev.olog.service.music.model.MusicNotificationState

interface INotification {

    companion object {
        const val NOTIFICATION_ID: Int = 0x6d7363
        const val CHANNEL_ID = "$NOTIFICATION_ID"
        const val IMAGE_SIZE = 200
    }

    suspend fun update(state: MusicNotificationState): Notification
    fun cancel()

}