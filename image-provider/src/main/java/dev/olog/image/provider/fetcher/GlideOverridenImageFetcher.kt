package dev.olog.image.provider.fetcher

import android.content.Context
import android.net.Uri
import android.webkit.URLUtil
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import dev.olog.core.MediaId
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.core.gateway.UsedImageGateway
import dev.olog.intents.AppConstants
import java.io.File
import java.io.InputStream

internal class GlideOverridenImageFetcher(
    private val context: Context,
    private val mediaId: MediaId,
    private val usedImageGateway: UsedImageGateway,
    private val songGateway: SongGateway,
    private val podcastGateway: PodcastGateway
) : DataFetcher<InputStream> {

    override fun getDataClass(): Class<InputStream> = InputStream::class.java

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        val inputStream: InputStream?
        if (mediaId.isLeaf) {
            inputStream = loadForSongs(mediaId)
        } else if (mediaId.isAlbum || mediaId.isPodcastAlbum) {
            inputStream = loadForAlbums(mediaId)
        } else if (mediaId.isArtist || mediaId.isPodcastArtist) {
            inputStream = loadForArtist(mediaId)
        } else {
            inputStream = null
        }
        callback.onDataReady(inputStream)
    }

    private fun loadForSongs(mediaId: MediaId): InputStream? {
        val trackImage = usedImageGateway.getForTrack(mediaId.resolveId)
        if (trackImage == null) {
            val albumId = if (mediaId.isPodcast) {
                podcastGateway.getByParam(mediaId.resolveId)?.albumId
            } else {
                songGateway.getByParam(mediaId.resolveId)?.albumId
            }
            if (albumId != null) {
                val albumImage = usedImageGateway.getForAlbum(albumId)
                return open(albumImage)
            }
            return null

        } else {
            return open(trackImage)
        }
    }


    private fun loadForAlbums(mediaId: MediaId): InputStream? {
        val albumImage = usedImageGateway.getForAlbum(mediaId.categoryId)
        return open(albumImage)
    }

    private fun loadForArtist(mediaId: MediaId): InputStream? {
        val artistImage = usedImageGateway.getForArtist(mediaId.categoryId)
        return open(artistImage)
    }

    private fun open(image: String?): InputStream? {
        if (image == null || image == AppConstants.NO_IMAGE){
            return null
        }
        if (URLUtil.isContentUrl(image)){
            return context.contentResolver.openInputStream(Uri.parse(image))
        }
        val file = File(image)
        if (file.exists()){
            return file.inputStream()
        }
        return null
    }

    override fun cleanup() {

    }

    override fun getDataSource(): DataSource = DataSource.LOCAL

    override fun cancel() {

    }
}