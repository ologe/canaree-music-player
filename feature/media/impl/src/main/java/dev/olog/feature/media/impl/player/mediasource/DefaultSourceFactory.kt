package dev.olog.feature.media.impl.player.mediasource

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.feature.media.impl.interfaces.ISourceFactory
import dev.olog.feature.media.impl.model.MediaEntity
import javax.inject.Inject

class DefaultSourceFactory @Inject constructor(
    @ApplicationContext context: Context

) : ISourceFactory<MediaEntity> {

    private val bandwidthMeter = DefaultBandwidthMeter.Builder(context).build()
    private val userAgent = Util.getUserAgent(context, "Canaree")
    private val dataSource = DefaultDataSourceFactory(context, userAgent, bandwidthMeter)
    private val extractorFactory = ProgressiveMediaSource.Factory(dataSource)

    override fun get(model: MediaEntity): MediaSource {
        val mediaSource = extractorFactory.createMediaSource(getTrackUri(model.id))
        return ConcatenatingMediaSource(mediaSource)
    }

    private fun getTrackUri(id: Long): MediaItem {
        val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
        return MediaItem.fromUri(uri)
    }

}