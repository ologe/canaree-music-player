package dev.olog.feature.media.player.mediasource

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.feature.media.interfaces.ISourceFactory
import dev.olog.feature.media.model.MediaEntity
import javax.inject.Inject

internal class DefaultSourceFactory @Inject constructor(
    @ApplicationContext context: Context

) : ISourceFactory<MediaEntity> {

    private val dataSource = DefaultDataSource.Factory(context)
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