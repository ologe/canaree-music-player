package dev.olog.service.music.player.crossfade

import android.content.Context
import androidx.core.math.MathUtils
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.injection.dagger.ServiceLifecycle
import dev.olog.service.music.EventDispatcher
import dev.olog.service.music.EventDispatcher.Event
import dev.olog.service.music.OnAudioSessionIdChangeListener
import dev.olog.service.music.interfaces.ExoPlayerListenerWrapper
import dev.olog.service.music.interfaces.IMaxAllowedPlayerVolume
import dev.olog.service.music.model.PlayerMediaEntity
import dev.olog.service.music.player.mediasource.ClippedSourceFactory
import dev.olog.shared.flowInterval
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class CrossFadePlayerImpl @Inject internal constructor(
    @ApplicationContext context: Context,
    @ServiceLifecycle lifecycle: Lifecycle,
    mediaSourceFactory: ClippedSourceFactory,
    musicPreferencesUseCase: MusicPreferencesGateway,
    private val eventDispatcher: EventDispatcher,
    private val volume: IMaxAllowedPlayerVolume,
    private val onAudioSessionIdChangeListener: OnAudioSessionIdChangeListener

) : AbsPlayer<CrossFadePlayerImpl.Model>(context, lifecycle, mediaSourceFactory, volume),
    ExoPlayerListenerWrapper,
    CoroutineScope by MainScope() {

    private var isCurrentSongPodcast = false

    private var fadeDisposable: Job? = null

    private var crossFadeTime = 0

    init {
        player.addListener(this)
        player.playbackParameters = PlaybackParameters(1f, 1f, true)
        player.addAudioListener(onAudioSessionIdChangeListener)

        launch {
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

        launch {
            musicPreferencesUseCase.observeCrossFade()
                .collect { crossFadeTime = it }
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        player.removeListener(this)
        player.removeAudioListener(onAudioSessionIdChangeListener)
        cancelFade()
        cancel()
    }

    override fun setPlaybackSpeed(speed: Float) {
        // skip silence
        player.playbackParameters = PlaybackParameters(speed, 1f, false)
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
            crossFadeTime,
            volume.getMaxAllowedVolume()
        )
        player.volume = min

        fadeDisposable?.cancel()
        fadeDisposable = launch {
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
            time.toInt(),
            volume.getMaxAllowedVolume()
        )
        player.volume = max

        if (isCurrentSongPodcast) {
            return
        }

        fadeDisposable = launch {
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

    class Model(
        @JvmField
        val playerMediaEntity: PlayerMediaEntity,
        @JvmField
        private val trackEnded: Boolean,
        @JvmField
        private val crossFadeTime: Int
    ) {

        val mediaEntity = playerMediaEntity.mediaEntity
        val isFlac: Boolean = mediaEntity.path.endsWith(".flac")
        val duration: Long = mediaEntity.duration
        val isCrossFadeOn: Boolean = crossFadeTime > 0
        val isTrackEnded: Boolean = trackEnded && isCrossFadeOn
        val isGoodIdeaToClip = crossFadeTime >= 5000

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Model

            if (playerMediaEntity != other.playerMediaEntity) return false
            if (trackEnded != other.trackEnded) return false
            if (crossFadeTime != other.crossFadeTime) return false

            return true
        }

        override fun hashCode(): Int {
            var result = playerMediaEntity.hashCode()
            result = 31 * result + trackEnded.hashCode()
            result = 31 * result + crossFadeTime
            return result
        }

        fun copy(
            playerMediaEntity: PlayerMediaEntity = this.playerMediaEntity,
            trackEnded: Boolean = this.trackEnded,
            crossFadeTime: Int = this.crossFadeTime
        ) : Model {
            return Model(
                playerMediaEntity, trackEnded, crossFadeTime
            )
        }

    }

    private class CrossFadeInternals(duration: Int, maxVolumeAllowed: Float) {

        val min: Float = 0f
        val max: Float = maxVolumeAllowed
        val interval: Long = 200L
        private val times: Long = duration / interval
        val delta: Float = Math.abs(max - min) / times

        operator fun component1() = min
        operator fun component2() = max
        operator fun component3() = interval
        operator fun component4() = delta

    }

}