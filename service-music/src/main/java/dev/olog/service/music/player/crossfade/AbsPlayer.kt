package dev.olog.service.music.player.crossfade

import android.content.Context
import androidx.annotation.CallSuper
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import dev.olog.service.music.R
import dev.olog.service.music.interfaces.IPlayerDelegate
import dev.olog.service.music.interfaces.ISourceFactory
import dev.olog.service.music.player.PlayerVolume
import dev.olog.shared.android.extensions.toast
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.time.Duration
import kotlin.time.milliseconds

/**
 * This class handles playback
 */
internal abstract class AbsPlayer(
    lifecycleOwner: LifecycleOwner,
    private val context: Context,
    private val mediaSourceFactory: ISourceFactory<CrossFadePlayer.Model>,
    volume: PlayerVolume

) : IPlayerDelegate<CrossFadePlayer.Model>,
    Player.EventListener,
    DefaultLifecycleObserver {

    private val trackSelector = DefaultTrackSelector()
    private val factory = DefaultRenderersFactory(context).apply {
        setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)

    }
    protected val player: SimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context, factory, trackSelector)

    init {
        lifecycleOwner.lifecycle.addObserver(this)

        volume.volume
            .onEach(player::setVolume)
            .launchIn(lifecycleOwner.lifecycleScope)
    }

    @CallSuper
    override fun onDestroy(owner: LifecycleOwner) {
        player.release()
    }

    @CallSuper
    override fun prepare(model: CrossFadePlayer.Model, isTrackEnded: Boolean) {
        val mediaSource = mediaSourceFactory.get(model)
        player.setMediaSource(mediaSource, model.bookmark.toLongMilliseconds())
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
    override fun seekTo(where: Duration) {
        val safeSeek = where.coerceIn(0.milliseconds, getDuration())
        player.seekTo(safeSeek.toLongMilliseconds())
    }

    @CallSuper
    override fun isPlaying(): Boolean {
        return player.playWhenReady
    }

    @CallSuper
    override fun getBookmark(): Duration {
        return player.currentPosition.milliseconds
    }

    @CallSuper
    override fun getDuration(): Duration {
        return player.duration.milliseconds
    }

    @CallSuper
    override fun onPlayerError(error: ExoPlaybackException) {
        val what = when (error.type) {
            ExoPlaybackException.TYPE_SOURCE -> error.sourceException
            ExoPlaybackException.TYPE_RENDERER -> error.rendererException
            ExoPlaybackException.TYPE_UNEXPECTED -> error.unexpectedException
            else -> null
        }
        error.printStackTrace()
        what?.printStackTrace()

        context.applicationContext.toast(R.string.music_player_error)
    }

}