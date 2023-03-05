package dev.olog.service.music.player.crossfade

import android.app.Service
import android.util.Log
import androidx.annotation.CallSuper
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import dev.olog.service.music.R
import dev.olog.service.music.interfaces.IPlayerDelegate
import dev.olog.service.music.interfaces.IMaxAllowedPlayerVolume
import dev.olog.service.music.interfaces.ISourceFactory
import dev.olog.shared.android.extensions.lifecycleOwner
import dev.olog.shared.android.extensions.toast
import dev.olog.shared.android.utils.clamp

/**
 * This class handles playback
 */
internal abstract class AbsPlayer<T>(
    private val service: Service,
    private val mediaSourceFactory: ISourceFactory<T>,
    volume: IMaxAllowedPlayerVolume

) : IPlayerDelegate<T>,
    Player.Listener,
    DefaultLifecycleObserver {

    private val factory = DefaultRenderersFactory(service).apply {
        setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
    }
    protected val player = ExoPlayer.Builder(service, factory).build()

    init {
        service.lifecycleOwner.lifecycle.addObserver(this)

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
    override fun prepare(mediaEntity: T, bookmark: Long) {
        val mediaSource = mediaSourceFactory.get(mediaEntity)
        player.prepare(mediaSource)
        player.playWhenReady = false
        player.seekTo(bookmark)
    }

    @CallSuper
    override fun play(mediaEntity: T, hasFocus: Boolean, isTrackEnded: Boolean) {
        val mediaSource = mediaSourceFactory.get(mediaEntity)
        player.prepare(mediaSource, true, true)
        if (mediaEntity is CrossFadePlayer.Model){
            player.seekTo(mediaEntity.playerMediaEntity.bookmark)
        }
        player.playWhenReady = hasFocus
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
        val safeSeek = clamp(where, 0L, getDuration())
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
    override fun onPlayerError(error: PlaybackException) {
        error.printStackTrace()
        if (error is ExoPlaybackException) {
            val what = when (error.type) {
                ExoPlaybackException.TYPE_SOURCE -> error.sourceException.message
                ExoPlaybackException.TYPE_RENDERER -> error.rendererException.message
                ExoPlaybackException.TYPE_UNEXPECTED -> error.unexpectedException.message
                else -> "Unknown: $error"
            }
            Log.e("Player", "onPlayerError $what")
        }
        service.applicationContext.toast(R.string.music_player_error)
    }

}