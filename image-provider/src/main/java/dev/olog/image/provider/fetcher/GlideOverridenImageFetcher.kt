package dev.olog.image.provider.fetcher

import android.content.Context
import android.net.Uri
import android.webkit.URLUtil
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import dev.olog.core.MediaId
import dev.olog.core.gateway.UsedImageGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.SongGateway
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
        if (mediaId.isLeaf) {
            loadForSongs(mediaId, callback)
        } else if (mediaId.isAlbum || mediaId.isPodcastAlbum) {
            loadForAlbums(mediaId, callback)
        } else if (mediaId.isArtist || mediaId.isPodcastArtist) {
            loadForArtist(mediaId, callback)
        } else {
            callback.onLoadFailed(Exception("no override image"))
        }
    }

    private fun loadForSongs(mediaId: MediaId, callback: DataFetcher.DataCallback<in InputStream>) {
        val trackImage = usedImageGateway.getForTrack(mediaId.resolveId)
        if (trackImage == null) {
            val albumId = if (mediaId.isPodcast) {
                podcastGateway.getByParam(mediaId.resolveId)?.albumId
            } else {
                songGateway.getByParam(mediaId.resolveId)?.albumId
            }
            if (albumId != null) {
                val albumImage = usedImageGateway.getForAlbum(albumId)
                if (open(albumImage, callback)) {
                    return
                }
            }
        } else {
            if (open(trackImage, callback)) {
                return
            }
        }
        callback.onLoadFailed(Exception("no override image"))
    }


    private fun loadForAlbums(mediaId: MediaId, callback: DataFetcher.DataCallback<in InputStream>) {
        val albumImage = usedImageGateway.getForAlbum(mediaId.categoryId)
        open(albumImage, callback)
    }

    private fun loadForArtist(mediaId: MediaId, callback: DataFetcher.DataCallback<in InputStream>) {
        val artistImage = usedImageGateway.getForArtist(mediaId.categoryId)
        open(artistImage, callback)
    }

    private fun open(image: String?, callback: DataFetcher.DataCallback<in InputStream>): Boolean  {
        if (image == null){
            callback.onLoadFailed(Exception("no override image"))
            return true
        }
        if (image == AppConstants.NO_IMAGE){
            callback.onDataReady(null)
            return true
        }
        if (URLUtil.isContentUrl(image)){
            callback.onDataReady(context.contentResolver.openInputStream(Uri.parse(image)))
            return true
        }
        val file = File(image)
        if (file.exists()){
            callback.onDataReady(file.inputStream())
            return true
        }
        return false
    }

    override fun cleanup() {

    }

    override fun getDataSource(): DataSource = DataSource.LOCAL

    override fun cancel() {

    }
}