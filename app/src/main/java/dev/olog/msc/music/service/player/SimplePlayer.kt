package dev.olog.msc.music.service.player

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.media.AudioManager
import android.util.Log
import android.view.KeyEvent
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import dagger.Lazy
import dev.olog.msc.BuildConfig
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.music.service.equalizer.OnAudioSessionIdChangeListener
import dev.olog.msc.music.service.interfaces.ExoPlayerListenerWrapper
import dev.olog.msc.music.service.volume.IPlayerVolume
import dev.olog.msc.utils.k.extension.crashlyticsLog
import dev.olog.msc.utils.k.extension.dispatchEvent
import javax.inject.Inject

class SimplePlayer @Inject constructor(
        @ApplicationContext context: Context,
        lifecycle: Lifecycle,
        private val mediaSourceFactory: MediaSourceFactory,
        volume: IPlayerVolume,
        private val audioManager: Lazy<AudioManager>,
        private val onAudioSessionIdChangeListener: OnAudioSessionIdChangeListener

): CustomExoPlayer, DefaultLifecycleObserver, ExoPlayerListenerWrapper {

    private val trackSelector = DefaultTrackSelector()
    private val player = ExoPlayerFactory.newSimpleInstance(context, trackSelector)

    init {
        lifecycle.addObserver(this)

        volume.listener = object : IPlayerVolume.Listener {
            override fun onVolumeChanged(volume: Float) {
                player.volume = volume
            }
        }

        player.addListener(this)
        player.addAudioDebugListener(onAudioSessionIdChangeListener)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        player.removeListener(this)
        player.removeAudioDebugListener(onAudioSessionIdChangeListener)
        onAudioSessionIdChangeListener.release()
        player.release()
    }

    override fun prepare(songId: Long, bookmark: Long) {
        val mediaSource = mediaSourceFactory.get(songId)
        player.prepare(mediaSource)
        player.playWhenReady = false
        player.seekTo(bookmark)
    }

    override fun play(songId: Long, hasFocus: Boolean) {
        val mediaSource = mediaSourceFactory.get(songId)
        player.prepare(mediaSource, true, true)
        player.playWhenReady = hasFocus
    }

    override fun resume() {
        player.playWhenReady = true
    }

    override fun pause() {
        player.playWhenReady = false
    }

    override fun seekTo(where: Long) {
        player.seekTo(where)
    }

    override fun isPlaying(): Boolean {
        return player.playWhenReady
    }

    override fun getBookmark(): Long {
        return player.currentPosition
    }

    override fun getDuration(): Long {
        return player.duration
    }

    override fun setVolume(volume: Float) {
        player.volume = volume
    }

    override fun crossFade() {
        // not implemented
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        val what = when (error.type) {
            ExoPlaybackException.TYPE_SOURCE -> error.sourceException.message
            ExoPlaybackException.TYPE_RENDERER -> error.rendererException.message
            ExoPlaybackException.TYPE_UNEXPECTED -> error.unexpectedException.message
            else -> "Unknown: $error"
        }

        crashlyticsLog("player error $what")

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

}