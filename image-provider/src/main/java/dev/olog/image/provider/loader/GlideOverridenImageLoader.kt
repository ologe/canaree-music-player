package dev.olog.image.provider.loader

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import dev.olog.core.MediaId
import dev.olog.core.gateway.ImageVersionGateway
import dev.olog.core.gateway.UsedImageGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.image.provider.fetcher.GlideOverridenImageFetcher
import java.io.InputStream
import javax.inject.Inject

internal class GlideOverridenImageLoader(
    private val usedImageGateway: UsedImageGateway,
    private val songGateway: SongGateway,
    private val podcastGateway: PodcastGateway,
    private val imageVersionGateway: ImageVersionGateway
) : ModelLoader<MediaId, InputStream> {

    override fun buildLoadData(
        mediaId: MediaId,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream>? {

        val version = imageVersionGateway.getCurrentVersion(mediaId)

        return ModelLoader.LoadData(
            MediaIdKey(mediaId, version),
            GlideOverridenImageFetcher(
                mediaId, usedImageGateway, songGateway, podcastGateway
            )
        )
    }

    override fun handles(mediaId: MediaId): Boolean {
        return mediaId.isLeaf || mediaId.isAlbum || mediaId.isArtist ||
                mediaId.isPodcastAlbum || mediaId.isPodcastArtist
    }

    class Factory @Inject constructor(
        private val usedImageGateway: UsedImageGateway,
        private val songGateway: SongGateway,
        private val podcastGateway: PodcastGateway,
        private val imageVersionGateway: ImageVersionGateway

    ) : ModelLoaderFactory<MediaId, InputStream> {
        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<MediaId, InputStream> {
            return GlideOverridenImageLoader(
                usedImageGateway,
                songGateway,
                podcastGateway,
                imageVersionGateway
            )
        }

        override fun teardown() {

        }
    }

}

