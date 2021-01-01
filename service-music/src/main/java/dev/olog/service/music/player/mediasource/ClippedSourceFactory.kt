package dev.olog.service.music.player.mediasource

import com.google.android.exoplayer2.source.ClippingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import dev.olog.domain.prefs.MusicPreferencesGateway
import dev.olog.service.music.interfaces.ISourceFactory
import dev.olog.service.music.player.crossfade.CrossFadePlayer
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.seconds

internal class ClippedSourceFactory @Inject constructor (
    private val sourceFactory: DefaultSourceFactory,
    private val musicPrefs: MusicPreferencesGateway

) : ISourceFactory<CrossFadePlayer.Model> {

    companion object {
        private val clipStart = 2.seconds
        private val clipEnd = 4.seconds
    }

    // when gapless is on, clip mediaSource
    private val isGapless: Boolean
        get() = musicPrefs.isGapless


    /*
     * Clip the media source only when gapless is On,
     * otherwise fallback to default media source.
     * NB -> some Flac files are not seekable and clippable, and when clipped,
     *       an error is thrown, so flacs will never be clipped
     */
    override fun get(model: CrossFadePlayer.Model): MediaSource {
        val mediaSource = sourceFactory.get(model.mediaEntity)

        if (isGapless && model.isGoodIdeaToClip && !model.mediaEntity.isPodcast){
            if (model.isTrackEnded){
                // clip start and end
                return ClippingMediaSource(
                    mediaSource,
                    clipStart.inMicroseconds.toLong(),
                    calculateEndClip(model.duration).inMicroseconds.toLong(),
                )
            }
            // skipTo case, clip only the end
            return ClippingMediaSource(
                mediaSource,
                0,
                calculateEndClip(model.duration).inMicroseconds.toLong()
            )

        }

        return mediaSource
    }

    private fun calculateEndClip(trackDuration: Duration): Duration {
        return trackDuration - clipEnd
    }

}