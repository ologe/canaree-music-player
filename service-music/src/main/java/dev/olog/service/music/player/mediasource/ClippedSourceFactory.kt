package dev.olog.service.music.player.mediasource

import android.app.Service
import com.google.android.exoplayer2.source.ClippingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import dev.olog.core.prefs.MusicPreferencesGateway
import dev.olog.service.music.interfaces.ISourceFactory
import dev.olog.service.music.player.crossfade.CrossFadePlayer
import dev.olog.shared.android.extensions.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class ClippedSourceFactory @Inject constructor (
    service: Service,
    private val sourceFactory: DefaultSourceFactory,
    musicPrefsUseCase: MusicPreferencesGateway

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
        service.lifecycleScope.launch {
            musicPrefsUseCase.observeGapless()
                .collect { isGapless = it }
        }
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