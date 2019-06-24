package dev.olog.image.provider.loader

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import dev.olog.core.MediaId
import dev.olog.core.gateway.PodcastGateway2
import dev.olog.core.gateway.SongGateway2
import dev.olog.core.gateway.UsedImageGateway
import dev.olog.image.provider.fetcher.GlideOverridenImageFetcher
import java.io.InputStream
import javax.inject.Inject

internal class GlideOverridenImageLoader(
    private val usedImageGateway: UsedImageGateway,
    private val songGateway: SongGateway2,
    private val podcastGateway: PodcastGateway2
) : ModelLoader<MediaId, InputStream> {

    override fun buildLoadData(
        mediaId: MediaId,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream>? {
        return ModelLoader.LoadData(
            MediaIdKey(mediaId),
            GlideOverridenImageFetcher(
                mediaId,
                usedImageGateway,
                songGateway,
                podcastGateway
            )
        )
    }

    override fun handles(mediaId: MediaId): Boolean {
        return mediaId.isLeaf || mediaId.isAlbum || mediaId.isArtist ||
                mediaId.isPodcastAlbum || mediaId.isPodcastArtist
    }

    class Factory @Inject constructor(
        private val usedImageGateway: UsedImageGateway,
        private val songGateway: SongGateway2,
        private val podcastGateway: PodcastGateway2

    ) : ModelLoaderFactory<MediaId, InputStream> {
        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<MediaId, InputStream> {
            return GlideOverridenImageLoader(
                usedImageGateway,
                songGateway,
                podcastGateway
            )
        }

        override fun teardown() {

        }
    }

}

