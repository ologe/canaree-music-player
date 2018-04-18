package dev.olog.msc.music.service.player

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.google.android.exoplayer2.source.ClippingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.dagger.qualifier.ServiceLifecycle
import dev.olog.msc.domain.interactor.prefs.MusicPreferencesUseCase
import dev.olog.msc.music.service.model.MediaEntity
import dev.olog.msc.utils.k.extension.unsubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

interface SourceFactory {
    fun get(mediaEntity: MediaEntity) : MediaSource
}

class DefaultSourceFactory @Inject constructor(
        @ApplicationContext context: Context

): SourceFactory {

    private val bandwidthMeter = DefaultBandwidthMeter()
    private val userAgent = Util.getUserAgent(context, "Next")
    private val dataSource = DefaultDataSourceFactory(context, userAgent, bandwidthMeter)
    private val extractorFactory = ExtractorMediaSource.Factory(dataSource)

    override fun get(mediaEntity: MediaEntity) : MediaSource {
        return extractorFactory.createMediaSource(getTrackUri(mediaEntity.id))
    }

    private fun getTrackUri(id: Long): Uri {
        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
    }

}

class ClippedSourceFactory @Inject constructor (
        @ServiceLifecycle lifecycle: Lifecycle,
        private val sourceFactory: DefaultSourceFactory,
        musicPrefsUseCase: MusicPreferencesUseCase

) : DefaultLifecycleObserver, SourceFactory {

    companion object {
        private val clipStart = TimeUnit.SECONDS.toMicros(2)
        private val clipEnd = TimeUnit.SECONDS.toMicros(4)
    }

    // when gapless is on, clip mediaSource
    private var isGapless = false

    private val isGaplessDisposable = musicPrefsUseCase.observeGapless()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ isGapless = it }, Throwable::printStackTrace)

    init {
        lifecycle.addObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        isGaplessDisposable.unsubscribe()
    }

    override fun get(mediaEntity: MediaEntity): MediaSource {
        val mediaSource = sourceFactory.get(mediaEntity)

        if (!mediaEntity.isExplicit && isGapless){
            // isExplicit is used to know if track is ended by itself
            return ClippingMediaSource(mediaSource, 0, calculateEndClip(mediaEntity.duration))
        }

        return if (mediaEntity.isRemix && isGapless){
            // isRemix is used to know if crossFade is active
            ClippingMediaSource(mediaSource, clipStart, calculateEndClip(mediaEntity.duration))
        } else {
            mediaSource
        }

    }

    private fun calculateEndClip(trackDuration: Long): Long {
        return TimeUnit.MILLISECONDS.toMicros(trackDuration) - clipEnd
    }

}