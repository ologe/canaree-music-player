package dev.olog.image.provider.fetcher

import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import dev.olog.core.MediaId
import dev.olog.core.gateway.UsedImageGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.SongGateway
import java.io.File
import java.io.InputStream

internal class GlideOverridenImageFetcher(
    private val mediaId: MediaId,
    private val usedImageGateway: UsedImageGateway,
    private val songGateway: SongGateway,
    private val podcastGateway: PodcastGateway
) : DataFetcher<InputStream> {

    override fun getDataClass(): Class<InputStream> = InputStream::class.java

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        val overrideImage = when {
            mediaId.isLeaf -> usedImageGateway.getForTrack(mediaId.resolveId)
                ?: tryGetForAlbum(mediaId)
            mediaId.isAlbum || mediaId.isPodcastAlbum -> usedImageGateway.getForAlbum(mediaId.categoryId)
            mediaId.isArtist || mediaId.isPodcastArtist -> usedImageGateway.getForArtist(mediaId.categoryId)
            else -> null
        }

        if (overrideImage != null) {
            val file = File(overrideImage)
            if (file.exists()) {
                callback.onDataReady(file.inputStream())
                return
            }
        }
        callback.onLoadFailed(Exception("no override image"))
    }

    private fun tryGetForAlbum(mediaId: MediaId): String? {
        val albumId = if (mediaId.isPodcast){
            podcastGateway.getByParam(mediaId.resolveId)?.albumId
        } else {
            songGateway.getByParam(mediaId.resolveId)?.albumId
        } ?: return null
        return usedImageGateway.getForAlbum(albumId)
    }

    override fun cleanup() {

    }

    override fun getDataSource(): DataSource = DataSource.LOCAL

    override fun cancel() {

    }
}