package dev.olog.msc.music.service.player.crossfade

import android.content.Context
import android.media.AudioManager
import android.view.KeyEvent
import androidx.core.math.MathUtils
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import dagger.Lazy
import dev.olog.core.dagger.ApplicationContext
import dev.olog.msc.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.domain.gateway.prefs.MusicPreferencesGateway
import dev.olog.msc.music.service.equalizer.OnAudioSessionIdChangeListener
import dev.olog.msc.music.service.interfaces.ExoPlayerListenerWrapper
import dev.olog.msc.music.service.model.PlayerMediaEntity
import dev.olog.msc.music.service.player.DefaultPlayer
import dev.olog.msc.music.service.player.media.source.ClippedSourceFactory
import dev.olog.msc.music.service.volume.IPlayerVolume
import dev.olog.msc.utils.k.extension.dispatchEvent
import dev.olog.shared.unsubscribe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class CrossFadePlayerImpl @Inject internal constructor(
    @ApplicationContext context: Context,
    @ServiceLifecycle lifecycle: Lifecycle,
    mediaSourceFactory: ClippedSourceFactory,
    musicPreferencesUseCase: MusicPreferencesGateway,
    private val audioManager: Lazy<AudioManager>,
    private val volume: IPlayerVolume,
    private val onAudioSessionIdChangeListener: OnAudioSessionIdChangeListener

): DefaultPlayer<CrossFadePlayerImpl.Model>(context, lifecycle, mediaSourceFactory, volume), ExoPlayerListenerWrapper {

    private var isCurrentSongPodcast = false

    private var fadeDisposable : Disposable? = null

    private var crossFadeTime = 0
    private val crossFadeDurationDisposable = musicPreferencesUseCase
            .observeCrossFade()
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


    init {
        player.addListener(this)
        player.playbackParameters = PlaybackParameters(1f, 1f, true)
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

    override fun setPlaybackSpeed(speed: Float) {
        // skip silence
        player.playbackParameters = PlaybackParameters(speed, 1f, false)
    }

    override fun play(mediaEntity: Model, hasFocus: Boolean, isTrackEnded: Boolean) {
        isCurrentSongPodcast = mediaEntity.mediaEntity.isPodcast
        cancelFade()
        val updatedModel = mediaEntity.copy(trackEnded = isTrackEnded, crossFadeTime = crossFadeTime)
        super.play(updatedModel, hasFocus, isTrackEnded)
        //        debug("play, fade in ${isTrackEnded && crossFadeTime > 0}")
        if (isTrackEnded && crossFadeTime > 0 && !isCurrentSongPodcast) {
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
        val (min, max, interval, delta) = CrossFadeInternals(crossFadeTime, volume.getVolume())
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

        val (min, max, interval, delta) = CrossFadeInternals(time.toInt(), volume.getVolume())
        player.volume = max

        if (isCurrentSongPodcast){
            return
        }

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

    private fun requestNextSong(){
//      audioManager.get().dispatchEvent(KeyEvent.KEYCODE_MEDIA_NEXT)
        audioManager.get().dispatchEvent(KeyEvent.KEYCODE_MEDIA_FAST_FORWARD)
    }

    data class Model(
            val playerMediaEntity: PlayerMediaEntity,
            private val trackEnded: Boolean,
            private val crossFadeTime: Int
    ) {

        val mediaEntity = playerMediaEntity.mediaEntity
        val isFlac: Boolean = mediaEntity.path.endsWith(".flac")
        val duration: Long = mediaEntity.duration
        val isCrossFadeOn: Boolean = crossFadeTime > 0
        val isTrackEnded: Boolean = trackEnded && isCrossFadeOn
        val isGoodIdeaToClip = crossFadeTime >= 5000

    }

    private class CrossFadeInternals(duration: Int, maxVolumeAllowed: Float) {

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

}