package dev.olog.image.provider.loader

import android.net.Uri
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import dev.olog.core.MediaId
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.image.provider.fetcher.GlideOriginalImageFetcher
import java.io.InputStream
import javax.inject.Inject

internal class GlideOriginalImageLoader(
    private val uriLoader: ModelLoader<Uri, InputStream>,
    private val songGateway: SongGateway,
    private val podcastGateway: PodcastGateway

) : ModelLoader<MediaId, InputStream> {

    override fun handles(mediaId: MediaId): Boolean {
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
        mediaId: MediaId,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream>? {

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

    ) : ModelLoaderFactory<MediaId, InputStream> {

        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<MediaId, InputStream> {
            val uriLoader = multiFactory.build(Uri::class.java, InputStream::class.java)
            return GlideOriginalImageLoader(
                uriLoader,
                songGateway,
                podcastGateway
            )
        }

        override fun teardown() {
        }
    }

}