package dev.olog.music_service.notification

import android.app.Notification
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Pair
import dev.olog.music_service.model.MediaEntity

interface INotification {

    companion object {
        const val NOTIFICATION_ID : Int = 0x6d7363
    }

    fun createIfNeeded()
    fun updateState(playbackState: PlaybackStateCompat)
    fun updateMetadata(mediaEntity: MediaEntity)

    fun update(state: Int): Pair<Notification, Int>

}