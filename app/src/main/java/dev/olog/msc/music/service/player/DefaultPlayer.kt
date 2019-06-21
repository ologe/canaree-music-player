package dev.olog.msc.music.service.player

import android.content.Context
import android.util.Log
import androidx.annotation.CallSuper
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.crashlytics.android.Crashlytics
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import dev.olog.msc.BuildConfig
import dev.olog.msc.R
import dev.olog.msc.music.service.interfaces.ExoPlayerListenerWrapper
import dev.olog.msc.music.service.player.crossfade.CrossFadePlayerImpl
import dev.olog.msc.music.service.player.media.source.SourceFactory
import dev.olog.msc.music.service.volume.IPlayerVolume
import dev.olog.shared.clamp
import dev.olog.msc.utils.k.extension.toast

abstract class DefaultPlayer<T>(
        private val context: Context,
        lifecycle: Lifecycle,
        private val mediaSourceFactory: SourceFactory<T>,
        volume: IPlayerVolume

) : CustomExoPlayer<T>,
        ExoPlayerListenerWrapper,
        DefaultLifecycleObserver {

    private val trackSelector = DefaultTrackSelector()
    private val factory = DefaultRenderersFactory(context, DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
    protected val player: SimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context, factory, trackSelector)

    init {
        lifecycle.addObserver(this)

        volume.listener = object : IPlayerVolume.Listener {
            override fun onVolumeChanged(volume: Float) {
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
        if (mediaEntity is CrossFadePlayerImpl.Model){
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
    override fun onPlayerError(error: ExoPlaybackException) {
        val what = when (error.type) {
            ExoPlaybackException.TYPE_SOURCE -> error.sourceException.message
            ExoPlaybackException.TYPE_RENDERER -> error.rendererException.message
            ExoPlaybackException.TYPE_UNEXPECTED -> error.unexpectedException.message
            else -> "Unknown: $error"
        }

        Crashlytics.logException(error)

        if (BuildConfig.DEBUG) {
            Log.e("Player", "onPlayerError $what")
        }
        context.applicationContext.toast(R.string.music_player_error)
    }

}