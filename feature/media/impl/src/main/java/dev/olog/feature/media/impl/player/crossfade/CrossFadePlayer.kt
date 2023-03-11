package dev.olog.feature.media.impl.player.crossfade

import android.app.Service
import androidx.core.math.MathUtils
import androidx.lifecycle.LifecycleOwner
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.feature.media.impl.EventDispatcher
import dev.olog.feature.media.impl.EventDispatcher.Event
import dev.olog.feature.media.impl.OnAudioSessionIdChangeListener
import dev.olog.feature.media.impl.interfaces.IMaxAllowedPlayerVolume
import dev.olog.feature.media.impl.model.PlayerMediaEntity
import dev.olog.feature.media.impl.player.mediasource.ClippedSourceFactory
import dev.olog.platform.extension.lifecycleScope
import dev.olog.shared.clamp
import dev.olog.shared.flowInterval
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.abs

/**
 * Implements gapless and crossfade and delegates playback calls to [AbsPlayer]
 */
class CrossFadePlayer @Inject constructor(
    private val service: Service,
    mediaSourceFactory: ClippedSourceFactory,
    musicPreferencesUseCase: MusicPreferencesGateway,
    private val eventDispatcher: EventDispatcher,
    private val volume: IMaxAllowedPlayerVolume,
    private val onAudioSessionIdChangeListener: OnAudioSessionIdChangeListener

) : AbsPlayer<CrossFadePlayer.Model>(service, mediaSourceFactory, volume),
    Player.Listener {

    companion object {
        private const val MIN_CROSSFADE_FOR_GAPLESS = 1500
        private const val MAX_CROSSFADE_FOR_GAPLESS = 2000
    }

    private var isCurrentSongPodcast = false

    private var fadeDisposable: Job? = null

    private var gapless = false
    private var crossFadeTime = 0

    init {
        player.addListener(this)
        player.setPlaybackParameters(PlaybackParameters(1f, 1f))
        player.addListener(onAudioSessionIdChangeListener)

        service.lifecycleScope.launch {
            flowInterval(1, TimeUnit.SECONDS)
                .filter { crossFadeTime > 0 } // crossFade enabled
                .filter { getDuration() > 0 && getBookmark() > 0 } // duration and bookmark strictly positive
                .filter { getDuration() > getBookmark() }
                .map { getDuration() - getBookmark() <= crossFadeTime }
                .distinctUntilChanged()
                .filter { it }
                .collect {
                    fadeOut(getDuration() - getBookmark())
                }
        }

        service.lifecycleScope.launch {
            musicPreferencesUseCase.observeCrossFade().combine(musicPreferencesUseCase.observeGapless())
            { crossfade, gapless ->
                if (gapless){
                    // force song preloading
                    clamp(crossfade, MIN_CROSSFADE_FOR_GAPLESS, Int.MAX_VALUE)
                } else {
                    crossfade
                }

            }.collect {
                crossFadeTime = it
            }
        }
        service.lifecycleScope.launch {
            musicPreferencesUseCase.observeGapless()
                .collect { gapless = it }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        player.removeListener(this)
        player.removeListener(onAudioSessionIdChangeListener)
        cancelFade()
    }

    override fun setPlaybackSpeed(speed: Float) {
        // skip silence
        player.setPlaybackParameters(PlaybackParameters(speed, 1f))
    }

    override fun play(mediaEntity: Model, hasFocus: Boolean, isTrackEnded: Boolean) {
        isCurrentSongPodcast = mediaEntity.mediaEntity.isPodcast
        cancelFade()
        val updatedModel =
            mediaEntity.copy(trackEnded = isTrackEnded, crossFadeTime = crossFadeTime)
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

    fun stop() {
//        debug("stop")
        player.stop()
        cancelFade()
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
//        debug("new state $playbackState")
        when (playbackState) {
            Player.STATE_ENDED -> {
                stop()
                if (crossFadeTime == 0) {
                    requestNextSong()
                }
            }
        }
    }

    private fun fadeIn() {
//        debug("fading in")
        cancelFade()
        val (min, max, interval, delta) = CrossFadeInternals(
            gapless = gapless,
            crossfade = crossFadeTime,
            duration = crossFadeTime,
            maxVolumeAllowed = volume.getMaxAllowedVolume()
        )
        player.volume = min

        fadeDisposable?.cancel()
        fadeDisposable = service.lifecycleScope.launch {
            flowInterval(interval, TimeUnit.MILLISECONDS)
                .takeWhile { player.volume < max }
                .collect {
                    val current = MathUtils.clamp(player.volume + delta, min, max)
                    player.volume = current
                }
        }
    }

    private fun fadeOut(time: Long) {
        val state = player.playbackState
        if (state == Player.STATE_IDLE || state == Player.STATE_ENDED) {
            return
        }

//        debug("fading out, was already fading?=$isFadingOut")
        fadeDisposable?.cancel()
        requestNextSong()

        val (min, max, interval, delta) = CrossFadeInternals(
            gapless = gapless,
            crossfade = crossFadeTime,
            duration = time.toInt(),
            maxVolumeAllowed = volume.getMaxAllowedVolume()
        )
        player.volume = max

        if (isCurrentSongPodcast) {
            return
        }

        fadeDisposable = service.lifecycleScope.launch {
            flowInterval(interval, TimeUnit.MILLISECONDS)
                .takeWhile { player.volume > min }
                .collect {
                    val current = MathUtils.clamp(player.volume - delta, min, max)
                    player.volume = current
                }
        }

    }

    private fun cancelFade() {
        fadeDisposable?.cancel()
    }

    private fun restoreDefaultVolume() {
        player.volume = volume.getMaxAllowedVolume()
    }

    private fun requestNextSong() {
        eventDispatcher.dispatchEvent(Event.TRACK_ENDED)
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

    private class CrossFadeInternals(
        private val gapless: Boolean,
        private val crossfade: Int,
        private val duration: Int,
        private val maxVolumeAllowed: Float
    ) {

        val min: Float
            get() {
                if (gapless && crossfade <= MAX_CROSSFADE_FOR_GAPLESS){
                    return clamp(maxVolumeAllowed * 0.75f, 0f, maxVolumeAllowed)
                }
                return 0f
            }
        val max: Float = maxVolumeAllowed
        val interval: Long = 200L
        private val times: Long = duration / interval
        val delta: Float = abs(max - min) / times

        operator fun component1() = min
        operator fun component2() = max
        operator fun component3() = interval
        operator fun component4() = delta

    }

}