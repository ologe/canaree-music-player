package dev.olog.msc.glide

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.webkit.URLUtil
import androidx.core.net.toUri
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import dev.olog.msc.domain.gateway.LastFmGateway
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import dev.olog.msc.utils.img.ImageUtils
import java.io.File
import java.io.InputStream
import java.security.MessageDigest

class GlideImageLoader(
        private val context: Context,
        private val lastFmGateway: LastFmGateway,
        private val uriLoader: ModelLoader<Uri, InputStream>

) : ModelLoader<DisplayableItem, InputStream> {

    override fun handles(model: DisplayableItem): Boolean = true

    override fun buildLoadData(model: DisplayableItem, width: Int, height: Int, options: Options): ModelLoader.LoadData<InputStream>? {
        val mediaId = model.mediaId

        if (isAsset(model)){
            return uriLoader.buildLoadData(Uri.parse(model.image), width, height, options)
        }

        if (mediaId.isAlbum || mediaId.isLeaf){
            return when {
                notAnImage(model) -> {
                    // song/album has not a default image, download
                    if (mediaId.isLeaf){
                        ModelLoader.LoadData(MediaIdKey(model.mediaId), GlideSongFetcher(context, model, lastFmGateway))
                    } else {
                        ModelLoader.LoadData(MediaIdKey(model.mediaId), GlideAlbumFetcher(context, model, lastFmGateway))
                    }
                }
                else -> {
                    // use default album image
                    uriLoader.buildLoadData(Uri.parse(model.image), width, height, options)
                }
            }
        }

        if (mediaId.isArtist){
            // download artist image
            return ModelLoader.LoadData(MediaIdKey(model.mediaId), GlideArtistFetcher(context, model, lastFmGateway))
        }

        // use merged image
        return uriLoader.buildLoadData(Uri.fromFile(File(model.image)), width, height, options)
    }

    private fun isAsset(model: DisplayableItem): Boolean {
        return URLUtil.isAssetUrl(model.image)
    }

    private fun notAnImage(model: DisplayableItem): Boolean {
        if (!URLUtil.isNetworkUrl(model.image)){
            return !ImageUtils.isRealImage(context, model.image)
        }
        return false
    }

    class Factory(
            private val context: Context,
            private val lastFmGateway: LastFmGateway

    ) : ModelLoaderFactory<DisplayableItem, InputStream> {

        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<DisplayableItem, InputStream> {
            val uriLoader = multiFactory.build(Uri::class.java, InputStream::class.java)
            return GlideImageLoader(context, lastFmGateway, uriLoader)
        }

        override fun teardown() {
        }
    }

}

private class MediaIdKey(private val mediaId: MediaId) : Key {

    override fun toString(): String {
        if (mediaId.isLeaf){
            return "${MediaIdCategory.SONGS}${mediaId.leaf}"
        }
        return "${mediaId.category}${mediaId.categoryValue}"
    }

    override fun equals(other: Any?): Boolean {
        if (other is MediaId){
            if (this.mediaId.isLeaf && other.isLeaf){
                // is song
                return this.mediaId.leaf == other.leaf
            }
            return this.mediaId.category == other.category &&
                    this.mediaId.categoryValue == other.categoryValue
        }
        return false
    }

    override fun hashCode(): Int {
        if (mediaId.isLeaf){
            var result = MediaIdCategory.SONGS.hashCode()
            result = 31 * result + mediaId.leaf!!.hashCode()
            return result
        }
        var result = mediaId.category.hashCode()
        result = 31 * result + mediaId.categoryValue.hashCode()
        return result
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(this.toString().toByteArray(Key.CHARSET))
    }

}
