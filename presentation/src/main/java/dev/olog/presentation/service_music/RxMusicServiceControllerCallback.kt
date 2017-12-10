package dev.olog.presentation.service_music

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import io.reactivex.Flowable
import io.reactivex.processors.BehaviorProcessor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RxMusicServiceControllerCallback @Inject constructor() {

    private val listener = Listener()

    private val playbackStatePublisher = BehaviorProcessor.create<PlaybackStateCompat>()
    private val metadataPublisher = BehaviorProcessor.create<MediaMetadataCompat>()
    private val repeatModePublisher = BehaviorProcessor.create<Int>()
    private val shuffleModePublisher = BehaviorProcessor.create<Int>()
    private val extrasPublisher = BehaviorProcessor.create<Bundle>()

    val metadata: MediaMetadataCompat?
        get() = metadataPublisher.value

    val playbackState: PlaybackStateCompat?
        get() = playbackStatePublisher.value

    fun registerCallback(controller: MediaControllerCompat) {
        initializePublishers(controller)
        controller.registerCallback(listener)
    }

    fun unregisterCallback(controller: MediaControllerCompat) {
        controller.unregisterCallback(listener)
    }

    private fun initializePublishers(controller: MediaControllerCompat) {
        val playbackState = controller.playbackState
        if (playbackState != null) {
            playbackStatePublisher.onNext(playbackState)
        }

        val metadata = controller.metadata
        if (metadata != null) {
            metadataPublisher.onNext(metadata)
        }

        val extras = controller.extras
        if (extras != null) {
            extrasPublisher.onNext(extras)
        }

        repeatModePublisher.onNext(controller.repeatMode)
        shuffleModePublisher.onNext(controller.shuffleMode)
    }

    fun onPlaybackStateChanged(): Flowable<PlaybackStateCompat> {
        return playbackStatePublisher.share()
    }

    fun onMetadataChanged(): Flowable<MediaMetadataCompat> {
        return metadataPublisher.share()
    }

    fun onRepeatModeChanged(): Flowable<Int> {
        return repeatModePublisher.share()
    }

    fun onShuffleModeChanged(): Flowable<Int> {
        return shuffleModePublisher.share()
    }

    fun onExtrasChanged(): Flowable<Bundle> {
        return extrasPublisher.share()
    }

    private inner class Listener : MediaControllerCompat.Callback() {

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            if (state != null) {
                playbackStatePublisher.onNext(state)
            }
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            if (metadata != null) {
                metadataPublisher.onNext(metadata)
            }
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            repeatModePublisher.onNext(repeatMode)
        }

        override fun onShuffleModeChanged(shuffleMode: Int) {
            shuffleModePublisher.onNext(shuffleMode)
        }

        override fun onExtrasChanged(extras: Bundle?) {
            if (extras == null) return
            extrasPublisher.onNext(extras)
        }
    }


}
