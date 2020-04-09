package dev.olog.feature.service.music.player.mediasource

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import dev.olog.feature.service.music.interfaces.ISourceFactory
import dev.olog.feature.service.music.model.MediaEntity
import javax.inject.Inject

internal class DefaultSourceFactory @Inject constructor(
    context: Context

) : ISourceFactory<MediaEntity> {

    private val bandwidthMeter = DefaultBandwidthMeter.Builder(context).build()
    private val userAgent = Util.getUserAgent(context, "Canaree")
    private val dataSource = DefaultDataSourceFactory(context, userAgent, bandwidthMeter)
    private val extractorFactory = ProgressiveMediaSource.Factory(dataSource)

    override fun get(model: MediaEntity): MediaSource {
        if (model.previewUrl != Uri.EMPTY) {
            return extractorFactory.createMediaSource(model.previewUrl)
        }
        val mediaSource = extractorFactory.createMediaSource(getTrackUri(model.id))
        return ConcatenatingMediaSource(mediaSource)
    }

    private fun getTrackUri(id: Long): Uri {
        return ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
    }

}