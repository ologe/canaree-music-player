package dev.olog.msc.music.service.player

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.support.v4.media.session.PlaybackStateCompat
import dagger.Lazy
import dev.olog.msc.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.domain.interactor.prefs.MusicPreferencesUseCase
import dev.olog.msc.music.service.Noisy
import dev.olog.msc.music.service.PlayerState
import dev.olog.msc.music.service.focus.AudioFocusBehavior
import dev.olog.msc.music.service.interfaces.Player
import dev.olog.msc.music.service.interfaces.PlayerLifecycle
import dev.olog.msc.music.service.interfaces.ServiceLifecycleController
import dev.olog.msc.music.service.model.PlayerMediaEntity
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PlayerImpl @Inject constructor(
        @ServiceLifecycle lifecycle: Lifecycle,
        private val playerState: PlayerState,
        private val noisy: Lazy<Noisy>,
        private val serviceLifecycle: ServiceLifecycleController,
        private val audioFocus : AudioFocusBehavior,
        private val player: CustomExoPlayer,
        playerFading: PlayerFading,
        musicPreferencesUseCase: MusicPreferencesUseCase

) : Player,
        DefaultLifecycleObserver,
        PlayerLifecycle {

    private val listeners = mutableListOf<PlayerLifecycle.Listener>()

    private var crossFadeTime = 0
    private val crossFadeDurationDisposable = musicPreferencesUseCase
            .observeCrossFade(true)
            .subscribe({ crossFadeTime = it }, Throwable::printStackTrace)

    private var timeDisposable = Observable.interval(1, TimeUnit.SECONDS, Schedulers.computation())
            .filter { (player.getDuration() - player.getBookmark()) < crossFadeTime }
            .map { player.getDuration() - player.getBookmark() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ player.crossFade() }, Throwable::printStackTrace)

    init {
        lifecycle.addObserver(this)
        playerFading.setPlayerLifecycle(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        listeners.clear()
        releaseFocus()
        timeDisposable.unsubscribe()
        crossFadeDurationDisposable.unsubscribe()
    }

    override fun prepare(pairSongBookmark: Pair<PlayerMediaEntity, Long>) {
        val (entity, positionInQueue) = pairSongBookmark.first
        val bookmark = pairSongBookmark.second
        player.prepare(entity.id, bookmark)

        playerState.prepare(entity.id, bookmark)
        playerState.toggleSkipToActions(positionInQueue)

        listeners.forEach { it.onPrepare(entity) }
    }

    override fun playNext(playerModel: PlayerMediaEntity, nextTo: Boolean) {
        playerState.skipTo(nextTo)
        play(playerModel)
    }

    override fun play(playerModel: PlayerMediaEntity) {
        val hasFocus = requestFocus()

        val entity = playerModel.mediaEntity

        player.play(entity.id, hasFocus)

        val state = playerState.update(if (hasFocus) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED,
                0, entity.id)

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
        val playbackState = playerState.update(PlaybackStateCompat.STATE_PLAYING, getBookmark())
        listeners.forEach {
            it.onStateChanged(playbackState)
        }

        serviceLifecycle.start()
        noisy.get().register()
    }

    override fun pause(stopService: Boolean) {
        player.pause()
        val playbackState = playerState.update(PlaybackStateCompat.STATE_PAUSED, getBookmark())
        listeners.forEach {
            it.onStateChanged(playbackState)
        }
        noisy.get().unregister()
        releaseFocus()

        if (stopService) {
            serviceLifecycle.stop()
        }
    }

    override fun seekTo(millis: Long) {
        player.seekTo(millis)
        val state = if (isPlaying()) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED
        val playbackState = playerState.update(state, millis)
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