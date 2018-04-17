package dev.olog.msc.music.service.player

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
import dev.olog.msc.music.service.model.MediaEntity
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

class ClippedSourceFactory @Inject constructor(
        private val sourceFactory: DefaultSourceFactory

) : SourceFactory {

    companion object {
        private val clipStart = TimeUnit.SECONDS.toMicros(2)
        private val clipEnd = TimeUnit.SECONDS.toMicros(4)
        private val totalClip = clipStart + clipEnd

        fun getRealDuration(duration: Long): Long {
            return duration + totalClip
        }
    }

    override fun get(mediaEntity: MediaEntity): MediaSource {
        val mediaSource = sourceFactory.get(mediaEntity)
        return if (mediaEntity.isRemix){
            // isRemix is used to know if crossFade is active
            ClippingMediaSource(mediaSource, clipStart, calculateEndClip(mediaEntity.duration))
        } else {
            sourceFactory.get(mediaEntity)
        }

    }

    private fun calculateEndClip(trackDuration: Long): Long {
        return TimeUnit.MILLISECONDS.toNanos(trackDuration - clipEnd)
    }

}