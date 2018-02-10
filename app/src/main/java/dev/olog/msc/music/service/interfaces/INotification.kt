package dev.olog.msc.music.service.interfaces

import android.app.Notification
import android.support.v4.media.session.PlaybackStateCompat
import dev.olog.msc.music.service.model.MediaEntity

interface INotification {

    companion object {
        const val NOTIFICATION_ID : Int = 0x6d7363
        const val IMAGE_SIZE = 200
    }

    fun createIfNeeded()
    fun updateState(playbackState: PlaybackStateCompat)
    fun updateMetadata(metadata: MediaEntity)

    fun update(): Notification
    fun cancel()

}