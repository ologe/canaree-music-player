package dev.olog.image.provider.fetcher

import android.content.Context
import com.bumptech.glide.Priority
import com.bumptech.glide.load.data.DataFetcher
import dev.olog.core.MediaId
import dev.olog.core.gateway.LastFmGateway2
import java.io.InputStream

class GlideAlbumFetcher(
    context: Context,
    mediaId: MediaId,
    private val lastFmGateway: LastFmGateway2

) : BaseDataFetcher(context) {

    private val id = mediaId.resolveId

    override suspend fun execute(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>): String {
        return lastFmGateway.getAlbum(id)!!.image
    }


    override suspend fun mustFetch(): Boolean {
        return lastFmGateway.mustFetchAlbum(id)
    }

    override val threshold: Long = 600
}