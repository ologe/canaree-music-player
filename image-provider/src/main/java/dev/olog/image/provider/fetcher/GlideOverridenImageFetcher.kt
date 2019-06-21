package dev.olog.image.provider.fetcher

import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import dev.olog.core.MediaId
import dev.olog.core.gateway.PodcastGateway2
import dev.olog.core.gateway.SongGateway2
import java.io.InputStream

internal class GlideOverridenImageFetcher(
    private val mediaId: MediaId,
//    private val usedImageGateway: UsedImageGateway,
    private val songGateway: SongGateway2,
    private val podcastGateway: PodcastGateway2
) : DataFetcher<InputStream> {

    override fun getDataClass(): Class<InputStream> = InputStream::class.java

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
//        TODO
//        val inputStream: InputStream?
//        if (mediaId.isLeaf) {
//            inputStream = loadForSongs(mediaId)
//        } else if (mediaId.isAlbum || mediaId.isPodcastAlbum) {
//            inputStream = loadForAlbums(mediaId)
//        } else if (mediaId.isArtist || mediaId.isPodcastArtist) {
//            inputStream = loadForArtist(mediaId)
//        } else {
//            inputStream = null
//        }
//        callback.onDataReady(inputStream)
    }

//    private fun loadForSongs(mediaId: MediaId): InputStream? {
//        val trackImage = usedImageGateway.getForTrack(mediaId.resolveId)
//        if (trackImage == null) {
//            val albumId = if (mediaId.isPodcast) {
//                podcastGateway.getByParam(mediaId.resolveId).getItem()?.albumId
//            } else {
//                songGateway.getByParam(mediaId.resolveId).getItem()?.albumId
//            }
//            if (albumId != null) {
//                val albumImage = usedImageGateway.getForAlbum(albumId)
//                if (albumImage != null) {
//                    return File(albumImage).inputStream()
//                }
//
//            }
//            return null
//
//        } else {
//            return File(trackImage).inputStream()
//        }
//    }
//
//    private fun loadForAlbums(mediaId: MediaId): InputStream? {
//        val albumImage = usedImageGateway.getForAlbum(mediaId.categoryId)
//        if (albumImage != null) {
//            return File(albumImage).inputStream()
//        }
//        return null
//    }
//
//    private fun loadForArtist(mediaId: MediaId): InputStream? {
//        val artistImage = usedImageGateway.getForArtist(mediaId.categoryId)
//        if (artistImage != null) {
//            return File(artistImage).inputStream()
//        }
//        return null
//    }
//
    override fun cleanup() {

    }

    override fun getDataSource(): DataSource = DataSource.LOCAL

    override fun cancel() {

    }
}