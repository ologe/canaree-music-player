package dev.olog.image.provider.loader

import android.content.Context
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import dev.olog.core.MediaId
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.image.provider.fetcher.GlideOriginalImageFetcher
import java.io.InputStream
import javax.inject.Inject

internal class GlideOriginalImageLoader(
    private val context: Context,
    private val songGateway: SongGateway,
    private val podcastGateway: PodcastGateway

) : ModelLoader<MediaId, InputStream> {

    override fun handles(mediaId: MediaId): Boolean {
        if (mediaId.isLeaf) {
            return true
        }
        if (mediaId.isAlbum || mediaId.isPodcastAlbum) {
            return true
        }

        return false
    }

    override fun buildLoadData(
        mediaId: MediaId,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream>? {

        // retrieve image store on track
        return ModelLoader.LoadData(
            MediaIdKey(mediaId),
            GlideOriginalImageFetcher(
                context,
                mediaId,
                songGateway,
                podcastGateway
            )
        )
    }

    class Factory @Inject constructor(
        @ApplicationContext private val context: Context,
        private val songGateway: SongGateway,
        private val podcastGateway: PodcastGateway
    ) : ModelLoaderFactory<MediaId, InputStream> {

        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<MediaId, InputStream> {
            return GlideOriginalImageLoader(
                context,
                songGateway,
                podcastGateway
            )
        }

        override fun teardown() {
        }
    }

}