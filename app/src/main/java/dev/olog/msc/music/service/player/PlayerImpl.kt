package dev.olog.msc.music.service.player

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.media.AudioManager
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.KeyEvent
import com.crashlytics.android.Crashlytics
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import dagger.Lazy
import dev.olog.msc.BuildConfig
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.music.service.Noisy
import dev.olog.msc.music.service.PlayerState
import dev.olog.msc.music.service.equalizer.OnAudioSessionIdChangeListener
import dev.olog.msc.music.service.focus.AudioFocusBehavior
import dev.olog.msc.music.service.interfaces.ExoPlayerListenerWrapper
import dev.olog.msc.music.service.interfaces.Player
import dev.olog.msc.music.service.interfaces.PlayerLifecycle
import dev.olog.msc.music.service.interfaces.ServiceLifecycleController
import dev.olog.msc.music.service.model.PlayerMediaEntity
import dev.olog.msc.music.service.volume.IPlayerVolume
import dev.olog.msc.utils.k.extension.dispatchEvent
import javax.inject.Inject

class PlayerImpl @Inject constructor(
        @ApplicationContext private val context: Context,
        @ServiceLifecycle lifecycle: Lifecycle,
        private val audioManager: Lazy<AudioManager>,
        private val playerState: PlayerState,
        private val noisy: Lazy<Noisy>,
        private val serviceLifecycle: ServiceLifecycleController,
        volume: IPlayerVolume,
        private val onAudioSessionIdChangeListener: OnAudioSessionIdChangeListener,
        private val mediaSourceFactory: MediaSourceFactory,
        private val audioFocus : AudioFocusBehavior

) : Player,
        DefaultLifecycleObserver,
        ExoPlayerListenerWrapper,
        PlayerLifecycle {

    private val listeners = mutableListOf<PlayerLifecycle.Listener>()
    private val trackSelector = DefaultTrackSelector()
    private val exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector)

    init {
        lifecycle.addObserver(this)
        exoPlayer.addListener(this)
        volume.listener = object : IPlayerVolume.Listener {
            override fun onVolumeChanged(volume: Float) {
                exoPlayer.volume = volume
            }
        }

        exoPlayer.addAudioDebugListener(onAudioSessionIdChangeListener)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        listeners.clear()
        onAudioSessionIdChangeListener.release()
        exoPlayer.removeAudioDebugListener(onAudioSessionIdChangeListener)
        releaseFocus()
        exoPlayer.removeListener(this)
        exoPlayer.release()
    }

    override fun prepare(pairSongBookmark: Pair<PlayerMediaEntity, Long>) {
        val (entity, positionInQueue) = pairSongBookmark.first
        val bookmark = pairSongBookmark.second
        val mediaSource = mediaSourceFactory.get(entity.id)
        exoPlayer.prepare(mediaSource)
        exoPlayer.playWhenReady = false
        exoPlayer.seekTo(bookmark)

        playerState.prepare(entity.id, bookmark)
        playerState.toggleSkipToActions(positionInQueue)

        listeners.forEach { it.onPrepare(entity) }
    }

    override fun playNext(playerModel: PlayerMediaEntity, nextTo: Boolean) {
        playerState.skipTo(nextTo)
        play(playerModel)
    }

    override fun play(playerModel: PlayerMediaEntity) {
        val hasFocus = requestFocus()

        val entity = playerModel.mediaEntity

        val mediaSource = mediaSourceFactory.get(entity.id)
        exoPlayer.prepare(mediaSource, true, true)
        exoPlayer.playWhenReady = hasFocus

        val state = playerState.update(if (hasFocus) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED,
                0, entity.id)

        listeners.forEach {
            it.onStateChanged(state)
            it.onMetadataChanged(entity)
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
            it.onStateChanged(playbackState)
        }

        serviceLifecycle.start()
        noisy.get().register()
    }

    override fun pause(stopService: Boolean) {
        exoPlayer.playWhenReady = false
        val playbackState = playerState.update(PlaybackStateCompat.STATE_PAUSED, getBookmark())
        listeners.forEach {
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
            ExoPlaybackException.TYPE_SOURCE -> error.sourceException.message
            ExoPlaybackException.TYPE_RENDERER -> error.rendererException.message
            ExoPlaybackException.TYPE_UNEXPECTED -> error.unexpectedException.message
            else -> "Unknown: $error"
        }

        try {
            Crashlytics.log("player error $what")
        } catch (ex: Exception){}

        if (BuildConfig.DEBUG) {
            Log.e("Player", "onPlayerError $what")
        }
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if (playbackState == com.google.android.exoplayer2.Player.STATE_ENDED) {
//            audioManager.get().dispatchEvent(KeyEvent.KEYCODE_MEDIA_NEXT)
            audioManager.get().dispatchEvent(KeyEvent.KEYCODE_MEDIA_FAST_FORWARD)
        }
    }

    override fun isPlaying(): Boolean = exoPlayer.playWhenReady

    override fun getBookmark(): Long = exoPlayer.currentPosition

    override fun stopService() {
        serviceLifecycle.stop()
    }

    private fun requestFocus(): Boolean {
        return audioFocus.requestFocus()
    }

    private fun releaseFocus() {
        audioFocus.abandonFocus()
    }

    override fun addListener(listener: PlayerLifecycle.Listener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: PlayerLifecycle.Listener) {
        listeners.remove(listener)
    }

    override fun setVolume(volume: Float) {
        exoPlayer.volume = volume
    }
}