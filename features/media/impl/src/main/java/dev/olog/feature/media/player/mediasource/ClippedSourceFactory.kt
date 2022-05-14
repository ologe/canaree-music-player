package dev.olog.feature.media.player.mediasource

import com.google.android.exoplayer2.source.ClippingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import dev.olog.core.ServiceScope
import dev.olog.feature.media.api.MusicPreferencesGateway
import dev.olog.feature.media.interfaces.ISourceFactory
import dev.olog.feature.media.player.crossfade.CrossFadePlayer
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class ClippedSourceFactory @Inject constructor (
    private val sourceFactory: DefaultSourceFactory,
    musicPrefsUseCase: MusicPreferencesGateway,
    serviceScope: ServiceScope,
) : ISourceFactory<CrossFadePlayer.Model> {

    companion object {
        @JvmStatic
        private val clipStart = TimeUnit.SECONDS.toMicros(2)
        @JvmStatic
        private val clipEnd = TimeUnit.SECONDS.toMicros(4)
    }

    // when gapless is on, clip mediaSource
    private var isGapless = false

    init {
        musicPrefsUseCase.observeGapless()
            .onEach { isGapless = it }
            .launchIn(serviceScope)
    }


    /*
     * Clip the media source only when gapless is On,
     * otherwise fallback to default media source.
     * NB -> some Flac files are not seekable and clippable, and when clipped,
     *       an error is thrown, so flacs will never be clipped
     */
    override fun get(model: CrossFadePlayer.Model): MediaSource {
        val mediaSource = sourceFactory.get(model.mediaEntity)
        val isFlac = model.isFlac

        if (!isFlac && isGapless && model.isGoodIdeaToClip && !model.mediaEntity.isPodcast){
            if (model.isTrackEnded){
                // clip start and end
                return ClippingMediaSource(mediaSource,
                    clipStart, calculateEndClip(model.duration))
            }
            // skipTo case, clip only the end
            return ClippingMediaSource(mediaSource, 0, calculateEndClip(model.duration))

        }

        return mediaSource
    }

    private fun calculateEndClip(trackDuration: Long): Long {
        return TimeUnit.MILLISECONDS.toMicros(trackDuration) - clipEnd
    }

}