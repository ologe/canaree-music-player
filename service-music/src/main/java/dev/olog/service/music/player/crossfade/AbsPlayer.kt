package dev.olog.service.music.player.crossfade

import android.content.Context
import android.util.Log
import androidx.annotation.CallSuper
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import dev.olog.service.music.BuildConfig
import dev.olog.service.music.R
import dev.olog.service.music.interfaces.IPlayerDelegate
import dev.olog.service.music.interfaces.ExoPlayerListenerWrapper
import dev.olog.service.music.interfaces.IMaxAllowedPlayerVolume
import dev.olog.service.music.interfaces.ISourceFactory
import dev.olog.shared.android.extensions.toast

/**
 * This class handles playback
 */
internal abstract class AbsPlayer(
    private val context: Context,
    lifecycle: Lifecycle,
    private val mediaSourceFactory: ISourceFactory<CrossFadePlayer.Model>,
    volume: IMaxAllowedPlayerVolume

) : IPlayerDelegate<CrossFadePlayer.Model>,
    ExoPlayerListenerWrapper,
    DefaultLifecycleObserver {

    private val trackSelector = DefaultTrackSelector()
    private val factory = DefaultRenderersFactory(context).apply {
        setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)

    }
    protected val player: SimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context, factory, trackSelector)

    init {
        lifecycle.addObserver(this)

        volume.listener = object : IMaxAllowedPlayerVolume.Listener {
            override fun onMaxAllowedVolumeChanged(volume: Float) {
                player.volume = volume
            }
        }
    }

    @CallSuper
    override fun onDestroy(owner: LifecycleOwner) {
        player.release()
    }

    @CallSuper
    override fun prepare(model: CrossFadePlayer.Model, isTrackEnded: Boolean) {
        val mediaSource = mediaSourceFactory.get(model)
        player.setMediaSource(mediaSource, model.bookmark)
        player.prepare()
        player.playWhenReady = false
    }

    @CallSuper
    override fun resume() {
        player.playWhenReady = true
    }

    @CallSuper
    override fun pause() {
        player.playWhenReady = false
    }

    @CallSuper
    override fun seekTo(where: Long) {
        val safeSeek = where.coerceIn(0L, getDuration())
        player.seekTo(safeSeek)
    }

    @CallSuper
    override fun isPlaying(): Boolean {
        return player.playWhenReady
    }

    @CallSuper
    override fun getBookmark(): Long {
        return player.currentPosition
    }

    @CallSuper
    override fun getDuration(): Long {
        return player.duration
    }

    @CallSuper
    override fun setVolume(volume: Float) {
        player.volume = volume
    }

    @CallSuper
    override fun onPlayerError(error: ExoPlaybackException) {
        val what = when (error.type) {
            ExoPlaybackException.TYPE_SOURCE -> error.sourceException.message
            ExoPlaybackException.TYPE_RENDERER -> error.rendererException.message
            ExoPlaybackException.TYPE_UNEXPECTED -> error.unexpectedException.message
            else -> "Unknown: $error"
        }
        error.printStackTrace()

        if (BuildConfig.DEBUG) {
            Log.e("Player", "onPlayerError $what")
        }
        context.applicationContext.toast(R.string.music_player_error)
    }

}