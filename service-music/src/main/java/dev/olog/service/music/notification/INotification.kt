package dev.olog.service.music.notification

import dev.olog.service.music.player.InternalPlayerState

internal interface INotification {

    companion object {
        const val NOTIFICATION_ID: Int = 0x6d7363
        const val CHANNEL_ID = "$NOTIFICATION_ID"
        const val IMAGE_SIZE = 200
    }

    suspend fun update(data: InternalPlayerState.Data, isFavorite: Boolean)

    fun cancel()

}