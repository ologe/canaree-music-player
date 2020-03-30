package dev.olog.image.provider.loader

import android.content.Context
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory.*
import dev.olog.core.gateway.track.TrackGateway
import dev.olog.image.provider.fetcher.GlideOriginalImageFetcher
import dev.olog.shared.ApplicationContext
import java.io.InputStream
import javax.inject.Inject

private val allowedCategories = listOf(ALBUMS)
private val spotifyCategories = listOf(SPOTIFY_TRACK, SPOTIFY_ALBUMS)

internal class GlideOriginalImageLoader(
    private val context: Context,
    private val trackGateway: TrackGateway

) : ModelLoader<MediaId, InputStream> {

    override fun handles(mediaId: MediaId): Boolean {
        return when (mediaId) {
            is MediaId.Track -> mediaId.category !in spotifyCategories
            is MediaId.Category -> mediaId.category in allowedCategories
        }
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
                context = context,
                mediaId = mediaId,
                trackGateway = trackGateway
            )
        )
    }

    class Factory @Inject constructor(
        @ApplicationContext private val context: Context,
        private val trackGateway: TrackGateway
    ) : ModelLoaderFactory<MediaId, InputStream> {

        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<MediaId, InputStream> {
            return GlideOriginalImageLoader(
                context = context,
                trackGateway = trackGateway
            )
        }

        override fun teardown() {
        }
    }

}