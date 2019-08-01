package dev.olog.image.provider.loader

import android.net.Uri
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.image.provider.fetcher.GlideOriginalImageFetcher
import dev.olog.image.provider.model.OriginalImage
import java.io.InputStream
import javax.inject.Inject

/**
 * Simple wrapper that allows to get cached original image, because [GlideOriginalImageLoader]
 * has multiple loader type
 */
internal class GlideOriginalImageOnlyLoader(
    private val uriLoader: ModelLoader<Uri, InputStream>,
    private val songGateway: SongGateway,
    private val podcastGateway: PodcastGateway
) : ModelLoader<OriginalImage, InputStream> {

    override fun handles(originalImage: OriginalImage): Boolean {
        val mediaId = originalImage.mediaId
        if (mediaId.isArtist) {
            // artist image never exists on device
            return false
        }
        if (mediaId.isFolder || mediaId.isPlaylist || mediaId.isGenre || mediaId.isPodcastPlaylist) {
            return false
        }
        return true
    }

    override fun buildLoadData(
        model: OriginalImage,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream>? {
        val mediaId = model.mediaId
        if (mediaId.isAlbum || mediaId.isPodcastAlbum || mediaId.isLeaf) {
            // retrieve image store on track
            return ModelLoader.LoadData(
                MediaIdKey(mediaId),
                GlideOriginalImageFetcher(
                    mediaId,
                    songGateway,
                    podcastGateway
                )
            )
        }
        return uriLoader.buildLoadData(Uri.EMPTY, width, height, options)
    }

    class Factory @Inject constructor(
        private val songGateway: SongGateway,
        private val podcastGateway: PodcastGateway

    ) : ModelLoaderFactory<OriginalImage, InputStream> {

        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<OriginalImage, InputStream> {
            val uriLoader = multiFactory.build(Uri::class.java, InputStream::class.java)
            return GlideOriginalImageOnlyLoader(
                uriLoader,
                songGateway,
                podcastGateway
            )
        }

        override fun teardown() {
        }
    }

}