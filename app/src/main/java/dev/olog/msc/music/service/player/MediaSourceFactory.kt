package dev.olog.msc.music.service.player

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import dev.olog.msc.dagger.qualifier.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaSourceFactory @Inject constructor(
        @ApplicationContext context: Context
){

    private val bandwidthMeter = DefaultBandwidthMeter()
    private val userAgent = Util.getUserAgent(context, "Next")
    private val dataSource = DefaultDataSourceFactory(context, userAgent, bandwidthMeter)
    private val extractorFactory = ExtractorMediaSource.Factory(dataSource)


    fun get(id: Long) : MediaSource {
        return extractorFactory.createMediaSource(getTrackUri(id))
    }

    private fun getTrackUri(id: Long): Uri {
        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
    }

}