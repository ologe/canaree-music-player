package dev.olog.image.provider.fetcher

import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import dev.olog.core.MediaId
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.image.provider.executor.GlideScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.io.InputStream


class GlideOriginalImageFetcher(
    private val mediaId: MediaId,
    private val songGateway: SongGateway,
    private val podcastGateway: PodcastGateway

) : DataFetcher<InputStream>, CoroutineScope by GlideScope() {

    override fun getDataClass(): Class<InputStream> = InputStream::class.java
    override fun getDataSource(): DataSource = DataSource.LOCAL

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        launch {
            val id = getId()
            if (id == -1L) {
                callback.onLoadFailed(Exception("item not found for id$id"))
                return@launch
            }
            val itemPath: String?

            itemPath = when {
                mediaId.isLeaf && !mediaId.isPodcast -> songGateway.getByParam(id)?.path
                mediaId.isLeaf && mediaId.isPodcast -> podcastGateway.getByParam(id)?.path
                mediaId.isAlbum -> songGateway.getByAlbumId(id)?.path
                mediaId.isPodcastAlbum -> podcastGateway.getByAlbumId(id)?.path
                else -> {
                    callback.onLoadFailed(IllegalArgumentException("not a valid media id=$mediaId"))
                    return@launch
                }
            }
            yield()

            if (itemPath == null) {
                callback.onLoadFailed(IllegalArgumentException("track not found for id $id"))
                return@launch
            }
            try {
                val stream = OriginalImageFetcher.loadImage(itemPath)
                callback.onDataReady(stream)
            } catch (ex: Throwable) {
                if (ex is Exception){
                    callback.onLoadFailed(ex)
                } else {
                    ex.printStackTrace()
                }
            }
        }
    }



    private fun getId(): Long {
        var trackId = -1L
        if (mediaId.isLeaf) {
            trackId = mediaId.leaf!!
        } else if (mediaId.isAlbum || mediaId.isPodcastAlbum) {
            trackId = mediaId.categoryId
        }
        return trackId
    }

    override fun cleanup() {
        cancel(null)
    }

    override fun cancel() {
        cancel(null)
    }

}