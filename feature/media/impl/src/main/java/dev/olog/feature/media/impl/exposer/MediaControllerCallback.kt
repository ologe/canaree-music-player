package dev.olog.feature.media.impl.exposer

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import dev.olog.feature.media.api.controller.IMediaControllerCallback
import java.lang.IllegalStateException

internal class MediaControllerCallback(
    private val controllerCallback: IMediaControllerCallback

) : MediaControllerCompat.Callback() {

    override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
        controllerCallback.onMetadataChanged(metadata)
    }

    override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
        controllerCallback.onPlaybackStateChanged(state)
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        controllerCallback.onRepeatModeChanged(repeatMode)
    }

    override fun onShuffleModeChanged(shuffleMode: Int) {
        controllerCallback.onShuffleModeChanged(shuffleMode)
    }

    override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
        controllerCallback.onQueueChanged(queue)
    }

    override fun onQueueTitleChanged(title: CharSequence?) {
        throw IllegalStateException("not handled")
    }

    override fun onExtrasChanged(extras: Bundle?) {
        throw IllegalStateException("not handled")
    }


}