package dev.olog.msc.presentation.base.music.service

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat

class MediaServiceCallback(
        private val activity: MusicGlueActivity

) : MediaControllerCompat.Callback() {

    override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
        metadata?.let { activity.metadataPublisher.onNext(it) }
    }

    override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
        state?.let { activity.statePublisher.onNext(it) }
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        activity.repeatModePublisher.onNext(repeatMode)
    }

    override fun onShuffleModeChanged(shuffleMode: Int) {
        activity.shuffleModePublisher.onNext(shuffleMode)
    }

    override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
        queue?.let { activity.queuePublisher.onNext(it) }
    }

}