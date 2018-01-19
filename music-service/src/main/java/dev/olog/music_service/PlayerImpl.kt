package dev.olog.music_service

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.ContentUris
import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.KeyEvent
import com.crashlytics.android.Crashlytics
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import dagger.Lazy
import dev.olog.music_service.di.PerService
import dev.olog.music_service.di.ServiceLifecycle
import dev.olog.music_service.interfaces.ExoPlayerListenerWrapper
import dev.olog.music_service.interfaces.Player
import dev.olog.music_service.interfaces.PlayerLifecycle
import dev.olog.music_service.interfaces.ServiceLifecycleController
import dev.olog.music_service.model.PlayerMediaEntity
import dev.olog.music_service.utils.AudioFocusBehavior
import dev.olog.music_service.utils.dispatchEvent
import dev.olog.shared.ApplicationContext
import javax.inject.Inject

@PerService
class PlayerImpl @Inject constructor(
        @ApplicationContext private val context: Context,
        @ServiceLifecycle lifecycle: Lifecycle,
        private val audioManager: Lazy<AudioManager>,
        private val playerState: PlayerState,
        private val noisy: Lazy<Noisy>,
        private val serviceLifecycle: ServiceLifecycleController,
        private val volume: PlayerVolume

) : Player,
        DefaultLifecycleObserver,
        AudioManager.OnAudioFocusChangeListener,
        ExoPlayerListenerWrapper,
        PlayerLifecycle {

    private val listeners = mutableListOf<PlayerLifecycle.Listener>()

    private val extractorsFactory = DefaultExtractorsFactory()
    private val bandwidthMeter = DefaultBandwidthMeter()
    private val userAgent = Util.getUserAgent(context, "Msc")
    private val dataSource = DefaultDataSourceFactory(context, userAgent, bandwidthMeter)
    private val trackSelector = DefaultTrackSelector()
    private val exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector)

    init {
        lifecycle.addObserver(this)
        exoPlayer.addListener(this)
        volume.listener = object : PlayerVolume.Listener {
            override fun onVolumeChanged(volume: Float) {
                exoPlayer.volume = volume
            }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        listeners.clear()
        releaseFocus()
        exoPlayer.removeListener(this)
        exoPlayer.release()
    }

    override fun prepare(pairSongBookmark: Pair<PlayerMediaEntity, Long>) {
        val (entity, positionInQueue) = pairSongBookmark.first
        val bookmark = pairSongBookmark.second
        val mediaSource = createMediaSource(entity.id)
        exoPlayer.prepare(mediaSource)
        exoPlayer.playWhenReady = false
        exoPlayer.seekTo(bookmark)

        playerState.prepare(entity.id, bookmark)
        playerState.toggleSkipToActions(positionInQueue)

        listeners.forEach { it.onPrepare(entity) }
    }

    private fun createMediaSource(songId: Long): MediaSource {
        return ExtractorMediaSource(getTrackUri(songId),
                dataSource, extractorsFactory, null, null)
    }

    private fun getTrackUri(id: Long): Uri {
        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
    }

    override fun playNext(playerModel: PlayerMediaEntity, nextTo: Boolean) {
        playerState.skipTo(nextTo)
        play(playerModel)
    }

    override fun play(playerModel: PlayerMediaEntity) {
        val hasFocus = requestFocus()

        val entity = playerModel.mediaEntity

        val mediaSource = createMediaSource(entity.id)
        exoPlayer.prepare(mediaSource, true, true)
        exoPlayer.playWhenReady = hasFocus

        val state = playerState.update(if (hasFocus) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED,
                0, entity.id)

        listeners.forEach {
            it.onPlay(entity)
            it.onStateChanged(state)
        }

        playerState.toggleSkipToActions(playerModel.positionInQueue)
        noisy.get().register()

        serviceLifecycle.start()

    }

    override fun resume() {
        if (!requestFocus()) return

        exoPlayer.playWhenReady = true
        val playbackState = playerState.update(PlaybackStateCompat.STATE_PLAYING, getBookmark())
        listeners.forEach {
            it.onResume()
            it.onStateChanged(playbackState)
        }

        serviceLifecycle.start()
        noisy.get().register()
    }

    override fun pause(stopService: Boolean) {
        exoPlayer.playWhenReady = false
        val playbackState = playerState.update(PlaybackStateCompat.STATE_PAUSED, getBookmark())
        listeners.forEach {
            it.onPause()
            it.onStateChanged(playbackState)
        }
        noisy.get().unregister()
        releaseFocus()

        if (stopService) {
            serviceLifecycle.stop()
        }
    }

    override fun seekTo(millis: Long) {
        exoPlayer.seekTo(millis)
        val state = if (isPlaying()) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED
        val playbackState = playerState.update(state, millis)
        listeners.forEach {
            it.onStateChanged(playbackState)
        }

        if (isPlaying()) {
            serviceLifecycle.start()
        } else {
            serviceLifecycle.stop()
        }
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        val what = when (error.type) {
            ExoPlaybackException.TYPE_SOURCE -> {
                Crashlytics.logException(error.sourceException)
                error.sourceException.message
            }
            ExoPlaybackException.TYPE_RENDERER -> {
                Crashlytics.logException(error.rendererException)
                error.rendererException.message
            }
            ExoPlaybackException.TYPE_UNEXPECTED -> {
                Crashlytics.logException(error.unexpectedException)
                error.unexpectedException.message
            }
            else -> {
                Crashlytics.log("unexpected onPlayerError")
                "Unknown: " + error
            }
        }

        if (BuildConfig.DEBUG) {
            Log.e("Player", "onPlayerError " + what)
        }
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if (playbackState == com.google.android.exoplayer2.Player.STATE_ENDED) {
            audioManager.get().dispatchEvent(KeyEvent.KEYCODE_MEDIA_NEXT)
        }
    }

    override fun isPlaying(): Boolean = exoPlayer.playWhenReady

    override fun getBookmark(): Long = exoPlayer.currentPosition

    override fun stopService() {
        serviceLifecycle.stop()
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                exoPlayer.volume = this.volume.getNormalVolume()
            }
            AudioManager.AUDIOFOCUS_LOSS -> pause(false)
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> pause(false)
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                exoPlayer.volume = this.volume.getDuckingVolume()
            }
        }
    }

    private fun requestFocus(): Boolean {
        return AudioFocusBehavior.requestFocus(audioManager.get(), this)
    }

    private fun releaseFocus() {
        AudioFocusBehavior.abandonFocus(audioManager.get(), this)
    }

    override fun addListener(listener: PlayerLifecycle.Listener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: PlayerLifecycle.Listener) {
        listeners.remove(listener)
    }
}