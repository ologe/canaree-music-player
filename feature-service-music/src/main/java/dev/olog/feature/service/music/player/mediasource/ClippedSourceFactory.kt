package dev.olog.feature.service.music.player.mediasource

import android.net.Uri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import com.google.android.exoplayer2.source.ClippingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import dev.olog.domain.prefs.MusicPreferencesGateway
import dev.olog.core.dagger.ServiceLifecycle
import dev.olog.feature.service.music.interfaces.ISourceFactory
import dev.olog.feature.service.music.player.crossfade.CrossFadePlayer
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class ClippedSourceFactory @Inject constructor (
    @ServiceLifecycle private val lifecycle: Lifecycle,
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
        musicPrefsUseCase.observeGapless()
            .onEach { isGapless = it }
            .launchIn(lifecycle.coroutineScope)
    }

    /*
     * Clip the media source only when gapless is On,
     * otherwise fallback to default media source.
     * NB -> some Flac files are not seekable and clippable, and when clipped,
     *       an error is thrown, so flacs will never be clipped
     */
    override fun get(model: CrossFadePlayer.Model): MediaSource {
        val mediaSource = sourceFactory.get(model.mediaEntity)
        if (model.mediaEntity.previewUrl != Uri.EMPTY) {
            return mediaSource
        }

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