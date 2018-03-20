package dev.olog.msc.glide

import android.content.Context
import android.net.Uri
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey
import dev.olog.msc.domain.gateway.LastFmGateway
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.img.ImageUtils
import dev.olog.msc.utils.img.ImagesFolderUtils
import java.io.File
import java.io.InputStream

class GlideImageLoader(
        private val context: Context,
        private val lastFmGateway: LastFmGateway,
        private val uriLoader: ModelLoader<Uri, InputStream>

) : ModelLoader<DisplayableItem, InputStream> {

    override fun handles(model: DisplayableItem): Boolean = true

    override fun buildLoadData(model: DisplayableItem, width: Int, height: Int, options: Options): ModelLoader.LoadData<InputStream>? {
        val mediaId = model.mediaId

        if (mediaId.isAlbum || mediaId.isLeaf){
            if (notAnImage(model)){
                // song/album has not a default image, download
                return if (mediaId.isLeaf){
                    ModelLoader.LoadData(ObjectKey(model), GlideSongFetcher(model, lastFmGateway))
                } else {
                    ModelLoader.LoadData(ObjectKey(model), GlideAlbumFetcher(model, lastFmGateway))
                }
            } else {
                // use default album image
                return uriLoader.buildLoadData(Uri.parse(model.image), width, height, options)
            }
        }

        if (mediaId.isArtist){
            // download artist image
            return ModelLoader.LoadData(ObjectKey(model), GlideArtistFetcher(model, lastFmGateway))
        }

        // use merged image
        return uriLoader.buildLoadData(Uri.fromFile(File(model.image)), width, height, options)
    }

    private fun notAnImage(model: DisplayableItem): Boolean {
        val id = model.mediaId.resolveId

        if (model.image == ImagesFolderUtils.forAlbum(id)){
            return !ImageUtils.isRealImage(context, model.image)
        } // else already using a downloaded image or a local image
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
