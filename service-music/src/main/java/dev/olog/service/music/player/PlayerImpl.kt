package dev.olog.service.music.player

import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.injection.dagger.ServiceLifecycle
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.service.music.Noisy
import dev.olog.service.music.state.MusicServicePlaybackState
import dev.olog.service.music.focus.AudioFocusBehavior
import dev.olog.service.music.interfaces.*
import dev.olog.service.music.model.MetadataEntity
import dev.olog.service.music.model.PlayerMediaEntity
import dev.olog.service.music.model.SkipType
import dev.olog.shared.clamp
import dev.olog.shared.android.extensions.unsubscribe
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class PlayerImpl @Inject constructor(
    @ServiceLifecycle lifecycle: Lifecycle,
    private val playerState: MusicServicePlaybackState,
    private val noisy: Noisy,
    private val serviceLifecycle: ServiceLifecycleController,
    private val audioFocus : AudioFocusBehavior,
    private val player: CustomExoPlayer<PlayerMediaEntity>,
    musicPrefsUseCase: MusicPreferencesGateway,
    private val playerVolume: IMaxAllowedPlayerVolume

) : Player,
    DefaultLifecycleObserver,
    PlayerLifecycle,
    CoroutineScope by MainScope() {

    private val listeners = mutableListOf<PlayerLifecycle.Listener>()

    private var currentSpeed = 1f

    init {
        lifecycle.addObserver(this)

        launch {
            // TODO combine with max allowed volume changes
            musicPrefsUseCase.observeVolume()
                .flowOn(Dispatchers.Default)
                .collect { volume ->
                    val newVolume = volume.toFloat() / 100f * playerVolume.getMaxAllowedVolume()
                    player.setVolume(newVolume)
                }
        }

        launch {
            musicPrefsUseCase.observePlaybackSpeed()
                .collect {
                    currentSpeed = it
                    player.setPlaybackSpeed(it)
                    playerState.updatePlaybackSpeed(it)
                }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        listeners.clear()
        releaseFocus()
        cancel()
    }

    override fun prepare(playerModel: PlayerMediaEntity) {
        val entity = playerModel.mediaEntity
        player.prepare(playerModel, playerModel.bookmark)

        playerState.prepare(playerModel.bookmark)
        player.setPlaybackSpeed(currentSpeed)
        playerState.updatePlaybackSpeed(currentSpeed)
        playerState.toggleSkipToActions(playerModel.positionInQueue)

        listeners.forEach { it.onPrepare(MetadataEntity(playerModel.mediaEntity, SkipType.NONE)) }
    }

    override fun playNext(playerModel: PlayerMediaEntity, skipType: SkipType) {
        when (skipType){
            SkipType.NONE -> throw IllegalArgumentException("skip type must not be NONE")
            SkipType.RESTART,
            SkipType.SKIP_PREVIOUS -> playerState.skipTo(SkipType.SKIP_PREVIOUS)
            SkipType.SKIP_NEXT,
            SkipType.TRACK_ENDED -> playerState.skipTo(SkipType.SKIP_NEXT)
        }

        playInternal(playerModel, skipType)
    }

    override fun play(playerModel: PlayerMediaEntity) {
        playInternal(playerModel, SkipType.NONE)
    }

    private fun playInternal(playerModel: PlayerMediaEntity, skipType: SkipType){
        val hasFocus = requestFocus()

        val entity = playerModel.mediaEntity

        player.play(playerModel, hasFocus, skipType == SkipType.TRACK_ENDED)

        val state = playerState.update(
            if (hasFocus) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED,
            playerModel.bookmark, currentSpeed)

        listeners.forEach {
            it.onStateChanged(state)
            it.onMetadataChanged(MetadataEntity(entity, skipType))
        }

        playerState.toggleSkipToActions(playerModel.positionInQueue)
        noisy.register()

        serviceLifecycle.start()
    }

    override fun resume() {
        if (!requestFocus()) return

        player.resume()
        val playbackState = playerState.update(PlaybackStateCompat.STATE_PLAYING, getBookmark(), currentSpeed)
        listeners.forEach {
            it.onStateChanged(playbackState)
        }

        serviceLifecycle.start()
        noisy.register()
    }

    override fun pause(stopService: Boolean, releaseFocus: Boolean) {
        player.pause()
        val playbackState = playerState.update(PlaybackStateCompat.STATE_PAUSED, getBookmark(), currentSpeed)
        listeners.forEach {
            it.onStateChanged(playbackState)
        }
        noisy.unregister()

        if (releaseFocus){
            releaseFocus()
        }

        if (stopService) {
            serviceLifecycle.stop()
        }
    }

    override fun seekTo(millis: Long) {
        player.seekTo(millis)
        val state = if (isPlaying()) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED
        val playbackState = playerState.update(state, millis, currentSpeed)
        listeners.forEach {
            it.onStateChanged(playbackState)
            it.onSeek(millis)
        }

        if (isPlaying()) {
            serviceLifecycle.start()
        } else {
            serviceLifecycle.stop()
        }
    }

    override fun forwardTenSeconds() {
        val newBookmark = player.getBookmark() + TimeUnit.SECONDS.toMillis(10)
        seekTo(clamp(newBookmark, 0, player.getDuration()))
    }

    override fun replayTenSeconds() {
        val newBookmark = player.getBookmark() - TimeUnit.SECONDS.toMillis(10)
        seekTo(clamp(newBookmark, 0, player.getDuration()))
    }

    override fun forwardThirtySeconds() {
        val newBookmark = player.getBookmark() + TimeUnit.SECONDS.toMillis(30)
        seekTo(clamp(newBookmark, 0, player.getDuration()))
    }

    override fun replayThirtySeconds() {
        val newBookmark = player.getBookmark() - TimeUnit.SECONDS.toMillis(30)
        seekTo(clamp(newBookmark, 0, player.getDuration()))
    }

    override fun isPlaying(): Boolean = player.isPlaying()

    override fun getBookmark(): Long = player.getBookmark()

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
        player.setVolume(volume)
    }
}