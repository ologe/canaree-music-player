package dev.olog.service.music.player.crossfade

import android.content.Context
import androidx.annotation.CallSuper
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.domain.prefs.MusicPreferencesGateway
import dev.olog.service.music.EventDispatcher
import dev.olog.service.music.EventDispatcher.Event
import dev.olog.service.music.OnAudioSessionIdChangeListener
import dev.olog.service.music.model.MediaEntity
import dev.olog.service.music.model.PlayerMediaEntity
import dev.olog.service.music.player.PlayerVolume
import dev.olog.service.music.player.mediasource.ClippedSourceFactory
import dev.olog.shared.FlowInterval
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlin.math.abs
import kotlin.time.Duration
import kotlin.time.milliseconds
import kotlin.time.seconds

/**
 * Implements gapless and crossfade and delegates playback calls to [AbsPlayer]
 */
internal class CrossFadePlayer @Inject internal constructor(
    @ApplicationContext context: Context,
    lifecycleOwner: LifecycleOwner,
    mediaSourceFactory: ClippedSourceFactory,
    private val musicPrefs: MusicPreferencesGateway,
    private val eventDispatcher: EventDispatcher,
    private val volume: PlayerVolume,
    private val onAudioSessionIdChangeListener: OnAudioSessionIdChangeListener

) : AbsPlayer(
    lifecycleOwner = lifecycleOwner,
    context = context,
    mediaSourceFactory = mediaSourceFactory,
    volume = volume
), Player.EventListener {

    companion object {
        private val MIN_CROSSFADE_FOR_GAPLESS = 1500.milliseconds
        private val MAX_CROSSFADE_FOR_GAPLESS = 2000.milliseconds
    }

    private var isCurrentSongPodcast = false

    private val isGapless: Boolean
        get() = musicPrefs.isGapless

    private val crossFadeTime: Duration
        get() {
            val crossfade = musicPrefs.crossfade
            if (isGapless){
                // force track preloading
                return crossfade.coerceAtLeast(MIN_CROSSFADE_FOR_GAPLESS)
            } else {
                return crossfade
            }
        }

    init {
        player.addListener(this)
        player.setPlaybackParameters(PlaybackParameters(musicPrefs.getPlaybackSpeed(), 1f))

        player.addAudioListener(onAudioSessionIdChangeListener)

        FlowInterval(1.seconds)
            .filter { crossFadeTime.isPositive() } // crossFade enabled
            .filter { getDuration().isPositive() && getBookmark().isPositive() } // duration and bookmark strictly positive
            .filter { getDuration() > getBookmark() }
            .map { getDuration() - getBookmark() <= crossFadeTime }
            .distinctUntilChanged()
            .filter { it }
            .onEach { fadeOut(getDuration() - getBookmark()) }
            .launchIn(lifecycleOwner.lifecycleScope)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        player.removeListener(this)
        player.removeAudioListener(onAudioSessionIdChangeListener)
    }


    override var playbackSpeed: Float
        get() = player.playbackParameters.speed
        set(value) {
            player.setPlaybackParameters(PlaybackParameters(value, 1f))
        }

    override fun prepare(model: Model, isTrackEnded: Boolean) {
        isCurrentSongPodcast = model.mediaEntity.isPodcast

        val updatedModel = model.copy(trackEnded = isTrackEnded, crossFadeTime = crossFadeTime)
        super.prepare(updatedModel, isTrackEnded)

        if (isTrackEnded && crossFadeTime.isPositive() && !isCurrentSongPodcast) {
            fadeIn()
        } else {
            volume.restoreDefaultVolume()
        }
    }

    override fun seekTo(where: Duration) {
        volume.stopFadeAndRestoreVolume()
        super.seekTo(where)
    }

    override fun pause() {
        super.pause()
        volume.stopFadeAndRestoreVolume()
    }

    fun stop() {
        player.stop()
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            Player.STATE_ENDED -> {
                stop()
                if (crossFadeTime.toLongMilliseconds() == 0L) {
                    requestNextSong()
                }
            }
        }
    }

    private fun fadeIn() {
        val (min, max, interval, delta) = CrossFadeInternals(
            gapless = isGapless,
            crossfade = crossFadeTime,
            duration = crossFadeTime,
            maxVolumeAllowed = volume.maxAllowedVolume()
        )

        if (isCurrentSongPodcast) {
            setVolume(max)
            return
        }
        volume.fadeIn(interval, min, max, delta)
    }

    private fun fadeOut(time: Duration) {
        val state = player.playbackState
        if (state == Player.STATE_IDLE || state == Player.STATE_ENDED) {
            return
        }

        requestNextSong()

        val (min, max, interval, delta) = CrossFadeInternals(
            gapless = isGapless,
            crossfade = crossFadeTime,
            duration = time,
            maxVolumeAllowed = volume.maxAllowedVolume()
        )
        if (isCurrentSongPodcast) {
            setVolume(max)
            return
        }
        volume.fadeOut(interval, min, max, delta)

    }

    @CallSuper
    override fun setVolume(volume: Float) {
        this.volume.updateVolume(volume)
    }

    override fun setDucking(enabled: Boolean) {
        volume.setIsDucking(enabled)
    }

    private fun requestNextSong() {
        eventDispatcher.dispatchEvent(Event.TRACK_ENDED)
    }

    data class Model(
        private val playerMediaEntity: PlayerMediaEntity,
        private val trackEnded: Boolean,
        private val crossFadeTime: Duration
    ) {

        val mediaEntity: MediaEntity
            get() = playerMediaEntity.mediaEntity
        val bookmark: Duration
            get() = playerMediaEntity.bookmark

        val isFlac: Boolean = mediaEntity.path.endsWith(".flac")
        val duration: Duration = mediaEntity.duration
        val isCrossFadeOn: Boolean = crossFadeTime.isPositive()
        val isTrackEnded: Boolean = trackEnded && isCrossFadeOn
        val isGoodIdeaToClip = crossFadeTime >= 5000.milliseconds
    }

    private class CrossFadeInternals(
        private val gapless: Boolean,
        private val crossfade: Duration,
        duration: Duration,
        private val maxVolumeAllowed: Float
    ) {

        val min: Float
            get() {
                if (gapless && crossfade <= MAX_CROSSFADE_FOR_GAPLESS){
                    return (maxVolumeAllowed * 0.75f).coerceIn(0f, maxVolumeAllowed)
                }
                return 0f
            }
        val max: Float = maxVolumeAllowed
        val interval: Duration = 200.milliseconds
        private val times: Int = (duration / interval).toInt()
        val delta: Float = abs(max - min) / times

        operator fun component1() = min
        operator fun component2() = max
        operator fun component3() = interval
        operator fun component4() = delta

    }

}