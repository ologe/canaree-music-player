package dev.olog.service.music.player

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import dev.olog.domain.prefs.MusicPreferencesGateway
import dev.olog.service.music.Noisy
import dev.olog.service.music.focus.AudioFocusBehavior
import dev.olog.service.music.interfaces.IPlayer
import dev.olog.service.music.interfaces.IPlayerDelegate
import dev.olog.service.music.interfaces.IServiceLifecycleController
import dev.olog.service.music.model.PlayerMediaEntity
import dev.olog.service.music.model.SkipType
import dev.olog.service.music.state.MusicServicePlaybackState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.milliseconds
import kotlin.time.seconds

internal class PlayerImpl @Inject constructor(
    lifecycleOwner: LifecycleOwner,
    private val playerState: MusicServicePlaybackState,
    private val noisy: Noisy,
    private val serviceLifecycle: IServiceLifecycleController,
    private val audioFocus : AudioFocusBehavior,
    private val playerDelegate: IPlayerDelegate<PlayerMediaEntity>,
    musicPrefsUseCase: MusicPreferencesGateway,
    private val internalPlayerState: InternalPlayerState,
) : IPlayer {

    init {
        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                releaseFocus()
            }
        })

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

    override fun seekTo(millis: Duration) {
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
        val newBookmark = playerDelegate.getBookmark() + 10.seconds
        seekTo(newBookmark.coerceAtMost(playerDelegate.getDuration()))
    }

    override fun replayTenSeconds() {
        val newBookmark = playerDelegate.getBookmark() - 10.seconds
        seekTo(newBookmark.coerceAtLeast(0.milliseconds))
    }

    override fun forwardThirtySeconds() {
        val newBookmark = playerDelegate.getBookmark() + 30.seconds
        seekTo(newBookmark.coerceAtMost(playerDelegate.getDuration()))
    }

    override fun replayThirtySeconds() {
        val newBookmark = playerDelegate.getBookmark() - 30.seconds
        seekTo(newBookmark.coerceAtLeast(0.milliseconds))
    }

    override fun isPlaying(): Boolean = playerDelegate.isPlaying()

    override fun getBookmark(): Duration = playerDelegate.getBookmark()

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

    override fun setDucking(enabled: Boolean) {
        playerDelegate.setDucking(enabled)
    }
}