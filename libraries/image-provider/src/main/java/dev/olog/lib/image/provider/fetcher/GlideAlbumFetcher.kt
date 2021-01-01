package dev.olog.lib.image.provider.fetcher

import android.content.Context
import com.bumptech.glide.Priority
import com.bumptech.glide.load.data.DataFetcher
import dev.olog.core.mediaid.MediaId
import dev.olog.core.gateway.ImageRetrieverGateway
import java.io.InputStream

class GlideAlbumFetcher(
    context: Context,
    mediaId: MediaId,
    private val imageRetrieverGateway: ImageRetrieverGateway

) : BaseDataFetcher(context) {

    private val id = mediaId.resolveId

    override suspend fun execute(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>): String {
        return imageRetrieverGateway.getAlbum(id)!!.image
    }


    override suspend fun mustFetch(): Boolean {
        return imageRetrieverGateway.mustFetchAlbum(id)
    }

    override val threshold: Long = 600
}