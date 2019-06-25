package dev.olog.image.provider.loader

import android.content.Context
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import dev.olog.core.MediaId
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.gateway.LastFmGateway2
import dev.olog.image.provider.fetcher.GlideAlbumFetcher
import dev.olog.image.provider.fetcher.GlideArtistFetcher
import dev.olog.image.provider.fetcher.GlideSongFetcher
import java.io.InputStream
import javax.inject.Inject

internal class GlideLastFmImageLoader(
    private val context: Context,
    private val lastFmGateway: LastFmGateway2

) : ModelLoader<MediaId, InputStream> {

    override fun handles(mediaId: MediaId): Boolean {
        if (mediaId.isAnyPodcast) {
            return false
        }
        return mediaId.isLeaf || mediaId.isAlbum || mediaId.isArtist
    }

    override fun buildLoadData(
        mediaId: MediaId,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream>? {

        return if (mediaId.isLeaf) {
            // download track image
            ModelLoader.LoadData(
                MediaIdKey(mediaId),
                GlideSongFetcher(
                    context,
                    mediaId,
                    lastFmGateway
                )
            )
        } else if (mediaId.isAlbum) {
            // download album image
            ModelLoader.LoadData(
                MediaIdKey(mediaId),
                GlideAlbumFetcher(
                    context,
                    mediaId,
                    lastFmGateway
                )
            )
        } else {
            // download artist image
            ModelLoader.LoadData(
                MediaIdKey(mediaId),
                GlideArtistFetcher(
                    context,
                    mediaId,
                    lastFmGateway
                )
            )
        }
    }

    class Factory @Inject constructor(
            @ApplicationContext private val context: Context,
            private val lastFmGateway: LastFmGateway2

    ) : ModelLoaderFactory<MediaId, InputStream> {

        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<MediaId, InputStream> {
            return GlideLastFmImageLoader(
                context,
                lastFmGateway
            )
        }

        override fun teardown() {
        }
    }

}