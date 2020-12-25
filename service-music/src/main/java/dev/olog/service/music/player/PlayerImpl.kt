package dev.olog.service.music.player

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.service.music.Noisy
import dev.olog.service.music.focus.AudioFocusBehavior
import dev.olog.service.music.interfaces.IMaxAllowedPlayerVolume
import dev.olog.service.music.interfaces.IPlayer
import dev.olog.service.music.interfaces.IPlayerDelegate
import dev.olog.service.music.interfaces.IServiceLifecycleController
import dev.olog.service.music.model.PlayerMediaEntity
import dev.olog.service.music.model.SkipType
import dev.olog.service.music.state.MusicServicePlaybackState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class PlayerImpl @Inject constructor(
    lifecycleOwner: LifecycleOwner,
    private val playerState: MusicServicePlaybackState,
    private val noisy: Noisy,
    private val serviceLifecycle: IServiceLifecycleController,
    private val audioFocus : AudioFocusBehavior,
    private val playerDelegate: IPlayerDelegate<PlayerMediaEntity>,
    musicPrefsUseCase: MusicPreferencesGateway,
    private val playerVolume: IMaxAllowedPlayerVolume,
    private val internalPlayerState: InternalPlayerState,
) : IPlayer {

    init {
        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                releaseFocus()
            }
        })

        // TODO combine with max allowed volume changes
        musicPrefsUseCase.observeVolume()
            .flowOn(Dispatchers.Default)
            .onEach { volume ->
                val newVolume = volume.toFloat() / 100f * playerVolume.getMaxAllowedVolume()
                playerDelegate.setVolume(newVolume)
            }.launchIn(lifecycleOwner.lifecycleScope)

        musicPrefsUseCase.observePlaybackSpeed()
            .onEach {
                playerDelegate.playbackSpeed = it
                playerState.updatePlaybackSpeed(it)
            }.launchIn(lifecycleOwner.lifecycleScope)
    }

    override fun prepare(playerModel: PlayerMediaEntity, forcePause: Boolean) {
        val isPlaying = if (forcePause) false else playerDelegate.isPlaying()

        val isTrackEnded = playerModel.skipType == SkipType.TRACK_ENDED
        playerDelegate.prepare(playerModel, isTrackEnded)

        playerState.update(
            isPlaying = isPlaying,
            bookmark = playerModel.bookmark,
            speed = playerDelegate.playbackSpeed
        )

        internalPlayerState.prepare(
            entity = playerModel.mediaEntity,
            positionInQueue = playerModel.positionInQueue,
            bookmark = getBookmark(),
            skipType = playerModel.skipType,
            isPlaying = isPlaying
        )
    }

    override fun resume() {
        if (!requestFocus()) return

        playerDelegate.resume()
        playerState.update(
            isPlaying = true,
            bookmark = getBookmark(),
            speed = playerDelegate.playbackSpeed
        )

        serviceLifecycle.start()
        noisy.register()

        internalPlayerState.resume(
            bookmark = getBookmark()
        )
    }

    override fun pause(stopService: Boolean, releaseFocus: Boolean) {
        playerDelegate.pause()
        playerState.update(
            isPlaying = false,
            bookmark = getBookmark(),
            speed = playerDelegate.playbackSpeed
        )
        noisy.unregister()

        if (releaseFocus){
            releaseFocus()
        }

        if (stopService) {
            serviceLifecycle.stop()
        }

        internalPlayerState.pause(
            bookmark = getBookmark()
        )
    }

    override fun seekTo(millis: Long) {
        playerDelegate.seekTo(millis)
        playerState.update(
            isPlaying = isPlaying(),
            bookmark = millis,
            speed = playerDelegate.playbackSpeed
        )

        if (isPlaying()) {
            serviceLifecycle.start()
            internalPlayerState.resume(bookmark = getBookmark())
        } else {
            internalPlayerState.pause(bookmark = getBookmark())
            serviceLifecycle.stop()
        }
    }

    override fun forwardTenSeconds() {
        val newBookmark = playerDelegate.getBookmark() + TimeUnit.SECONDS.toMillis(10)
        seekTo(newBookmark.coerceIn(0, playerDelegate.getDuration()))
    }

    override fun replayTenSeconds() {
        val newBookmark = playerDelegate.getBookmark() - TimeUnit.SECONDS.toMillis(10)
        seekTo(newBookmark.coerceIn(0, playerDelegate.getDuration()))
    }

    override fun forwardThirtySeconds() {
        val newBookmark = playerDelegate.getBookmark() + TimeUnit.SECONDS.toMillis(30)
        seekTo(newBookmark.coerceIn(0, playerDelegate.getDuration()))
    }

    override fun replayThirtySeconds() {
        val newBookmark = playerDelegate.getBookmark() - TimeUnit.SECONDS.toMillis(30)
        seekTo(newBookmark.coerceIn(0, playerDelegate.getDuration()))
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

    override fun setVolume(volume: Float) {
        playerDelegate.setVolume(volume)
    }
}