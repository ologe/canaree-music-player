package dev.olog.image.provider.loader

import android.net.Uri
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import dev.olog.core.MediaId
import dev.olog.core.gateway.UsedImageGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.image.provider.fetcher.GlideOverridenImageFetcher
import dev.olog.intents.AppConstants
import java.io.InputStream
import javax.inject.Inject

internal class GlideOverridenImageLoader(
    private val uriLoader: ModelLoader<Uri, InputStream>,
    private val usedImageGateway: UsedImageGateway,
    private val songGateway: SongGateway,
    private val podcastGateway: PodcastGateway
) : ModelLoader<MediaId, InputStream> {

    override fun buildLoadData(
        mediaId: MediaId,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream>? {
        val overrideImage = when {
            mediaId.isLeaf -> usedImageGateway.getForTrack(mediaId.resolveId)
                ?: tryGetForAlbum(mediaId)
            mediaId.isAlbum || mediaId.isPodcastAlbum -> usedImageGateway.getForAlbum(mediaId.categoryId)
            mediaId.isArtist || mediaId.isPodcastArtist -> usedImageGateway.getForArtist(mediaId.categoryId)
            else -> null
        }

        return ModelLoader.LoadData(
            MediaIdKey(mediaId),
            GlideOverridenImageFetcher(overrideImage)
        )
    }

    private fun tryGetForAlbum(mediaId: MediaId): String? {
        val albumId = if (mediaId.isPodcast){
            podcastGateway.getByParam(mediaId.resolveId)?.albumId
        } else {
            songGateway.getByParam(mediaId.resolveId)?.albumId
        } ?: return null
        return usedImageGateway.getForAlbum(albumId)
    }

    override fun handles(mediaId: MediaId): Boolean {
        return mediaId.isLeaf || mediaId.isAlbum || mediaId.isArtist ||
                mediaId.isPodcastAlbum || mediaId.isPodcastArtist
    }

    class Factory @Inject constructor(
        private val usedImageGateway: UsedImageGateway,
        private val songGateway: SongGateway,
        private val podcastGateway: PodcastGateway

    ) : ModelLoaderFactory<MediaId, InputStream> {
        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<MediaId, InputStream> {
            val uriLoader = multiFactory.build(Uri::class.java, InputStream::class.java)
            return GlideOverridenImageLoader(
                uriLoader,
                usedImageGateway,
                songGateway,
                podcastGateway
            )
        }

        override fun teardown() {

        }
    }

}

