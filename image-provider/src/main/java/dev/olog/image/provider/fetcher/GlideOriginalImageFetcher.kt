package dev.olog.image.provider.fetcher

import android.content.Context
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import dev.olog.core.MediaId
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.SongGateway
import kotlinx.coroutines.*
import java.io.InputStream
import java.lang.RuntimeException

class GlideOriginalImageFetcher(
    private val context: Context,
    private val mediaId: MediaId,
    private val songGateway: SongGateway,
    private val podcastGateway: PodcastGateway

) : DataFetcher<InputStream> {

    override fun getDataClass(): Class<InputStream> = InputStream::class.java
    override fun getDataSource(): DataSource = DataSource.LOCAL

    override fun loadData(
        priority: Priority,
        callback: DataFetcher.DataCallback<in InputStream>
    ) = runBlocking {
        val id = getId()
        if (id == -1L) {
            callback.onLoadFailed(Exception("item not found for id$id"))
            return@runBlocking
        }

        val song: Song? = when {
            mediaId.isAlbum -> songGateway.getByAlbumId(id)
            mediaId.isPodcastAlbum -> podcastGateway.getByAlbumId(id)
            mediaId.isLeaf && !mediaId.isPodcast -> songGateway.getByParam(id)
            mediaId.isLeaf && mediaId.isPodcast -> podcastGateway.getByParam(id)
            else -> {
                callback.onLoadFailed(IllegalArgumentException("not a valid media id=$mediaId"))
                return@runBlocking
            }
        }
        yield()

        if (song == null) {
            callback.onLoadFailed(IllegalArgumentException("track not found for id $id"))
            return@runBlocking
        }
        try {
            val stream = OriginalImageFetcher.loadImage(context, song)
            callback.onDataReady(stream)
        } catch (ex: Throwable) {
            callback.onLoadFailed(RuntimeException(ex))
        }
    }



    private fun getId(): Long {
        if (mediaId.isAlbum || mediaId.isPodcastAlbum){
            return mediaId.categoryId
        }
        if (mediaId.isLeaf){
            return mediaId.leaf!!
        }
        return -1
    }

    override fun cleanup() {

    }

    override fun cancel() {

    }

}