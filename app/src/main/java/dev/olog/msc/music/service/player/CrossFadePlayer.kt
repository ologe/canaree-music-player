package dev.olog.msc.music.service.player

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.media.AudioManager
import android.support.v4.math.MathUtils
import android.view.KeyEvent
import com.google.android.exoplayer2.Player
import dagger.Lazy
import dev.olog.msc.BuildConfig
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.domain.interactor.prefs.MusicPreferencesUseCase
import dev.olog.msc.music.service.equalizer.OnAudioSessionIdChangeListener
import dev.olog.msc.music.service.interfaces.ExoPlayerListenerWrapper
import dev.olog.msc.music.service.model.MediaEntity
import dev.olog.msc.music.service.volume.IPlayerVolume
import dev.olog.msc.utils.assertMainThread
import dev.olog.msc.utils.k.extension.dispatchEvent
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private enum class CurrentPlayer {
    PLAYER_NOT_SET,
    PLAYER_ONE,
    PLAYER_TWO
}

class CrossFadePlayer @Inject constructor(
        private val playerOne: SimpleCrossFadePlayer,
        private val playerTwo: SimpleCrossFadePlayer

): CustomExoPlayer {

    private var current = CurrentPlayer.PLAYER_NOT_SET

    override fun prepare(mediaEntity: MediaEntity, bookmark: Long){
        assertMainThread()

        val player = getNextPlayer()
        player.prepare(mediaEntity, bookmark)
    }

    override fun play(mediaEntity: MediaEntity, hasFocus: Boolean, isTrackEnded: Boolean) {
        assertMainThread()
        val player = getNextPlayer()
        player.play(mediaEntity, hasFocus, isTrackEnded)
        if (!isTrackEnded){
            getSecondaryPlayer().stop()
        }
    }

    override fun resume() {
        getCurrentPlayer().resume()
    }

    override fun pause() {
        getCurrentPlayer().pause()
        getSecondaryPlayer().stop()
    }

    override fun seekTo(where: Long) {
        getCurrentPlayer().seekTo(where)
        getSecondaryPlayer().stop()
    }

    override fun isPlaying(): Boolean = getCurrentPlayer().isPlaying()
    override fun getBookmark(): Long = getCurrentPlayer().getBookmark()
    override fun getDuration(): Long = getCurrentPlayer().getDuration()

    override fun setVolume(volume: Float) {
        getCurrentPlayer().setVolume(volume)
        getSecondaryPlayer().setVolume(volume)
    }

    private fun getNextPlayer(): SimpleCrossFadePlayer {
        current = when (current){
            CurrentPlayer.PLAYER_NOT_SET,
            CurrentPlayer.PLAYER_TWO -> CurrentPlayer.PLAYER_ONE
            CurrentPlayer.PLAYER_ONE -> CurrentPlayer.PLAYER_TWO
        }

        return when (current){
            CurrentPlayer.PLAYER_ONE -> playerOne
            CurrentPlayer.PLAYER_TWO -> playerTwo
            else -> throw IllegalStateException("invalid current player")
        }
    }

    private fun getCurrentPlayer(): SimpleCrossFadePlayer {
        return when (current){
            CurrentPlayer.PLAYER_ONE -> playerOne
            CurrentPlayer.PLAYER_TWO -> playerTwo
            else -> throw IllegalStateException("invalid secondary player")
        }
    }

    private fun getSecondaryPlayer(): SimpleCrossFadePlayer{
        return when (current){
            CurrentPlayer.PLAYER_ONE -> playerTwo
            CurrentPlayer.PLAYER_TWO -> playerOne
            else -> throw IllegalStateException("invalid secondary player")
        }
    }

}

private var playerCount = 0

class SimpleCrossFadePlayer @Inject constructor(
        @ApplicationContext context: Context,
        @ServiceLifecycle lifecycle: Lifecycle,
        mediaSourceFactory: ClippedSourceFactory,
        musicPreferencesUseCase: MusicPreferencesUseCase,
        private val audioManager: Lazy<AudioManager>,
        private val volume: IPlayerVolume,
        private val onAudioSessionIdChangeListener: OnAudioSessionIdChangeListener

): DefaultPlayer(context, lifecycle, mediaSourceFactory, volume), ExoPlayerListenerWrapper {

    private var fadeDisposable : Disposable? = null

    private var crossFadeTime = 0
    private val crossFadeDurationDisposable = musicPreferencesUseCase
            .observeCrossFade(true)
            .subscribe({ crossFadeTime = it }, Throwable::printStackTrace)

    private val timeDisposable = Observable.interval(1, TimeUnit.SECONDS, Schedulers.computation())
            .filter { crossFadeTime > 0 } // crossFade enabled
            .filter { getDuration() > 0 && getBookmark() > 0 } // duration and bookmark strictly positive
            .filter { getDuration() > getBookmark() }
            .map { getDuration() - getBookmark() <= crossFadeTime }
            .distinctUntilChanged()
            .filter { it }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ fadeOut(getDuration() - getBookmark()) }, Throwable::printStackTrace)

    private val currentPlayerNumber = playerCount++

    init {
        player.addListener(this)

        player.addAudioDebugListener(onAudioSessionIdChangeListener)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        player.removeListener(this)
        player.removeAudioDebugListener(onAudioSessionIdChangeListener)
        cancelFade()
        timeDisposable.unsubscribe()
        crossFadeDurationDisposable.unsubscribe()
    }

    override fun play(mediaEntity: MediaEntity, hasFocus: Boolean, isTrackEnded: Boolean) {
        super.play(mediaEntity.copy(isRemix = crossFadeTime > 0, isExplicit = isTrackEnded && crossFadeTime > 0),
                hasFocus, isTrackEnded)
//        debug("play, fade in ${isTrackEnded && crossFadeTime > 0}")
        if (isTrackEnded && crossFadeTime > 0){
            fadeIn()
        } else {
            restoreDefaultVolume()
        }
    }

    override fun resume() {
//        debug("resume")
        cancelFade()
        restoreDefaultVolume()
        super.resume()
    }

    override fun pause() {
//        debug("pause")
        cancelFade()
        super.pause()
    }

    override fun seekTo(where: Long) {
//        debug("seekTo")
        cancelFade()
        restoreDefaultVolume()
        super.seekTo(where)
    }

    override fun setVolume(volume: Float) {
        cancelFade()
        super.setVolume(volume)
    }

    fun stop(){
//        debug("stop")
        player.stop()
        cancelFade()
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
//        debug("new state $playbackState")
        when (playbackState) {
            Player.STATE_ENDED -> {
                stop()
                if (crossFadeTime == 0){
                    requestNextSong()
                }
            }
        }
    }

    private fun fadeIn() {
//        debug("fading in")
        cancelFade()
        val (min, max, interval, delta) = CrossFadeModel(crossFadeTime, volume.getVolume())
        player.volume = min

        fadeDisposable = Observable.interval(interval, TimeUnit.MILLISECONDS, Schedulers.computation())
                .takeWhile { player.volume < max }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val current = MathUtils.clamp(player.volume + delta, min, max)
                    player.volume = current
                }, Throwable::printStackTrace)
    }

    private fun fadeOut(time: Long){
        val state = player.playbackState
        if (state == Player.STATE_IDLE || state == Player.STATE_ENDED){
            return
        }

//        debug("fading out, was already fading?=$isFadingOut")
        fadeDisposable.unsubscribe()
        requestNextSong()

        val (min, max, interval, delta) = CrossFadeModel(time.toInt(), volume.getVolume())
        player.volume = max

        fadeDisposable = Observable.interval(interval, TimeUnit.MILLISECONDS, Schedulers.computation())
                .takeWhile { player.volume > min }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val current = MathUtils.clamp(player.volume - delta, min, max)
                    player.volume = current
                }, Throwable::printStackTrace)
    }

    private fun cancelFade(){
        fadeDisposable.unsubscribe()
    }

    private fun restoreDefaultVolume() {
        player.volume = volume.getVolume()
    }

    private fun debug(message: String){
        if (BuildConfig.DEBUG){
            println("player $currentPlayerNumber, $message")
        }
    }

    private fun requestNextSong(){
//      audioManager.get().dispatchEvent(KeyEvent.KEYCODE_MEDIA_NEXT)
        audioManager.get().dispatchEvent(KeyEvent.KEYCODE_MEDIA_FAST_FORWARD)
    }

}

private class CrossFadeModel(duration: Int, maxVolumeAllowed: Float) {

    val min: Float = 0f
    val max: Float= maxVolumeAllowed
    val interval: Long = 200L
    private val times: Long = duration / interval
    val delta: Float = Math.abs(max - min) / times

    operator fun component1() = min
    operator fun component2() = max
    operator fun component3() = interval
    operator fun component4() = delta

}