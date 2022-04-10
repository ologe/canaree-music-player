package dev.olog.service.music.player

import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.olog.shared.android.ServiceLifecycle
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.service.music.Noisy
import dev.olog.service.music.state.MusicServicePlaybackState
import dev.olog.service.music.focus.AudioFocusBehavior
import dev.olog.service.music.interfaces.*
import dev.olog.service.music.model.MetadataEntity
import dev.olog.service.music.model.PlayerMediaEntity
import dev.olog.service.music.model.SkipType
import dev.olog.shared.clamp
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class PlayerImpl @Inject constructor(
    @ServiceLifecycle lifecycle: Lifecycle,
    private val playerState: MusicServicePlaybackState,
    private val noisy: Noisy,
    private val serviceLifecycle: IServiceLifecycleController,
    private val audioFocus : AudioFocusBehavior,
    private val playerDelegate: IPlayerDelegate<PlayerMediaEntity>,
    musicPrefsUseCase: MusicPreferencesGateway,
    private val playerVolume: IMaxAllowedPlayerVolume

) : IPlayer,
    DefaultLifecycleObserver,
    IPlayerLifecycle,
    CoroutineScope by MainScope() {

    private val listeners = mutableListOf<IPlayerLifecycle.Listener>()

    private var currentSpeed = 1f

    init {
        lifecycle.addObserver(this)

        launch {
            // TODO combine with max allowed volume changes
            musicPrefsUseCase.observeVolume()
                .flowOn(Dispatchers.Default)
                .collect { volume ->
                    val newVolume = volume.toFloat() / 100f * playerVolume.getMaxAllowedVolume()
                    playerDelegate.setVolume(newVolume)
                }
        }

        launch {
            musicPrefsUseCase.observePlaybackSpeed()
                .collect {
                    currentSpeed = it
                    playerDelegate.setPlaybackSpeed(it)
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
        playerDelegate.prepare(playerModel, playerModel.bookmark)

        playerState.prepare(playerModel.bookmark)
        playerDelegate.setPlaybackSpeed(currentSpeed)
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

        playerDelegate.play(playerModel, hasFocus, skipType == SkipType.TRACK_ENDED)

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

        playerDelegate.resume()
        val playbackState = playerState.update(PlaybackStateCompat.STATE_PLAYING, getBookmark(), currentSpeed)
        listeners.forEach {
            it.onStateChanged(playbackState)
        }

        serviceLifecycle.start()
        noisy.register()
    }

    override fun pause(stopService: Boolean, releaseFocus: Boolean) {
        playerDelegate.pause()
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
        playerDelegate.seekTo(millis)
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
        val newBookmark = playerDelegate.getBookmark() + TimeUnit.SECONDS.toMillis(10)
        seekTo(clamp(newBookmark, 0, playerDelegate.getDuration()))
    }

    override fun replayTenSeconds() {
        val newBookmark = playerDelegate.getBookmark() - TimeUnit.SECONDS.toMillis(10)
        seekTo(clamp(newBookmark, 0, playerDelegate.getDuration()))
    }

    override fun forwardThirtySeconds() {
        val newBookmark = playerDelegate.getBookmark() + TimeUnit.SECONDS.toMillis(30)
        seekTo(clamp(newBookmark, 0, playerDelegate.getDuration()))
    }

    override fun replayThirtySeconds() {
        val newBookmark = playerDelegate.getBookmark() - TimeUnit.SECONDS.toMillis(30)
        seekTo(clamp(newBookmark, 0, playerDelegate.getDuration()))
    }

    override fun isPlaying(): Boolean = playerDelegate.isPlaying()

    override fun getBookmark(): Long = playerDelegate.getBookmark()

    override fun stopService() {
        serviceLifecycle.stop()
    }

    private fun requestFocus(): Boolean {
        return audioFocus.requestFocus()
    }

    private fun releaseFocus() {
        audioFocus.abandonFocus()
    }

    override fun addListener(listener: IPlayerLifecycle.Listener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: IPlayerLifecycle.Listener) {
        listeners.remove(listener)
    }

    override fun setVolume(volume: Float) {
        playerDelegate.setVolume(volume)
    }
}