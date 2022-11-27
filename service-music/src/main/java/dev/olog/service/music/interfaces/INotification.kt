package dev.olog.service.music.interfaces

import android.app.Notification
import dev.olog.image.provider.loading.ImageSize
import dev.olog.service.music.model.MusicNotificationState

internal interface INotification {

    companion object {
        const val NOTIFICATION_ID: Int = 0x6d7363
        const val CHANNEL_ID = "$NOTIFICATION_ID"
        val IMAGE_SIZE = ImageSize.Original
    }

    suspend fun update(state: MusicNotificationState): Notification
    fun cancel()

}