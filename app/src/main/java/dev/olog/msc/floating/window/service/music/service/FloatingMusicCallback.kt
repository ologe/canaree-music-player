package dev.olog.msc.floating.window.service.music.service

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import dev.olog.msc.floating.window.service.service.MusicServiceBinder

class FloatingMusicCallback(
        private val binder: MusicServiceBinder

) : MediaControllerCompat.Callback() {

    override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
        metadata?.let { binder.metadataPublisher.onNext(it) }
    }

    override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
        state?.let { binder.statePublisher.onNext(it) }
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        binder.repeatModePublisher.onNext(repeatMode)
    }

    override fun onShuffleModeChanged(shuffleMode: Int) {
        binder.shuffleModePublisher.onNext(shuffleMode)
    }

    override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
        queue?.let { binder.queuePublisher.onNext(it) }
    }

}