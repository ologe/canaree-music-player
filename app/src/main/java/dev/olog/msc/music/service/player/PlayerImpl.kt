package dev.olog.msc.music.service.player

import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dagger.Lazy
import dev.olog.msc.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.domain.gateway.prefs.MusicPreferencesGateway
import dev.olog.msc.music.service.Noisy
import dev.olog.msc.music.service.PlayerState
import dev.olog.msc.music.service.focus.AudioFocusBehavior
import dev.olog.msc.music.service.interfaces.Player
import dev.olog.msc.music.service.interfaces.PlayerLifecycle
import dev.olog.msc.music.service.interfaces.ServiceLifecycleController
import dev.olog.msc.music.service.interfaces.SkipType
import dev.olog.msc.music.service.model.PlayerMediaEntity
import dev.olog.shared.clamp
import dev.olog.msc.utils.k.extension.unsubscribe
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PlayerImpl @Inject constructor(
        @ServiceLifecycle lifecycle: Lifecycle,
        private val playerState: PlayerState,
        private val noisy: Lazy<Noisy>,
        private val serviceLifecycle: ServiceLifecycleController,
        private val audioFocus : AudioFocusBehavior,
        private val player: CustomExoPlayer<PlayerMediaEntity>,
        musicPrefsUseCase: MusicPreferencesGateway

) : Player,
        DefaultLifecycleObserver,
        PlayerLifecycle {

    private val listeners = mutableListOf<PlayerLifecycle.Listener>()

    private var currentSpeed = 1f

    private val playerVolumeDisposable = musicPrefsUseCase.observePlaybackSpeed()
            .subscribe({
                currentSpeed = it
                player.setPlaybackSpeed(it)
                playerState.updatePlaybackSpeed(it)
            }, Throwable::printStackTrace)

    init {
        lifecycle.addObserver(this)
//        playerFading.setPlayerLifecycle(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        listeners.clear()
        playerVolumeDisposable.unsubscribe()
        releaseFocus()
    }

    override fun prepare(playerModel: PlayerMediaEntity) {
        val entity = playerModel.mediaEntity
        player.prepare(playerModel, playerModel.bookmark)

        playerState.prepare(entity.id, playerModel.bookmark)
        player.setPlaybackSpeed(currentSpeed)
        playerState.updatePlaybackSpeed(currentSpeed)
        playerState.toggleSkipToActions(playerModel.positionInQueue)

        listeners.forEach { it.onPrepare(entity) }
    }

    override fun playNext(playerModel: PlayerMediaEntity, skipType: SkipType) {
        when (skipType){
            SkipType.SKIP_PREVIOUS -> playerState.skipTo(false)
            SkipType.SKIP_NEXT,
            SkipType.TRACK_ENDED -> playerState.skipTo(true)
            else -> throw IllegalStateException("skip type can not be NONE")
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

        val state = playerState.update(if (hasFocus) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED,
                playerModel.bookmark, entity.id, currentSpeed)

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

        player.resume()
        val playbackState = playerState.update(PlaybackStateCompat.STATE_PLAYING, getBookmark(), currentSpeed)
        listeners.forEach {
            it.onStateChanged(playbackState)
        }

        serviceLifecycle.start()
        noisy.get().register()
    }

    override fun pause(stopService: Boolean, releaseFocus: Boolean) {
        player.pause()
        val playbackState = playerState.update(PlaybackStateCompat.STATE_PAUSED, getBookmark(), currentSpeed)
        listeners.forEach {
            it.onStateChanged(playbackState)
        }
        noisy.get().unregister()

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