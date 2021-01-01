package dev.olog.lib.image.provider.fetcher

import android.content.Context
import com.bumptech.glide.Priority
import com.bumptech.glide.load.data.DataFetcher
import dev.olog.domain.gateway.ImageRetrieverGateway
import dev.olog.domain.mediaid.MediaId
import java.io.InputStream

class GlideSongFetcher(
    context: Context,
    mediaId: MediaId.Track,
    private val imageRetrieverGateway: ImageRetrieverGateway

) : BaseDataFetcher(context) {

    private val id = mediaId.id

    override suspend fun execute(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>): String {
        return imageRetrieverGateway.getTrack(id)!!.image
    }

    override suspend fun mustFetch(): Boolean {
        return imageRetrieverGateway.mustFetchTrack(id)
    }

    override val threshold: Long = 600L
}