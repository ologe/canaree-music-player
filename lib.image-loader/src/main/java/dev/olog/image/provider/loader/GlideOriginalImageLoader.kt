package dev.olog.image.provider.loader

import android.content.Context
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import dev.olog.domain.MediaId
import dev.olog.domain.MediaIdCategory.*
import dev.olog.domain.gateway.track.TrackGateway
import dev.olog.domain.schedulers.Schedulers
import dev.olog.image.provider.fetcher.GlideOriginalImageFetcher
import java.io.InputStream
import javax.inject.Inject

private val allowedCategories = listOf(ALBUMS)
private val spotifyCategories = listOf(SPOTIFY_TRACK, SPOTIFY_ALBUMS)

internal class GlideOriginalImageLoader(
    private val context: Context,
    private val trackGateway: TrackGateway,
    private val schedulers: Schedulers
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
                trackGateway = trackGateway,
                schedulers = schedulers
            )
        )
    }

    class Factory @Inject constructor(
        private val context: Context,
        private val trackGateway: TrackGateway,
        private val schedulers: Schedulers
    ) : ModelLoaderFactory<MediaId, InputStream> {

        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<MediaId, InputStream> {
            return GlideOriginalImageLoader(
                context = context,
                trackGateway = trackGateway,
                schedulers = schedulers
            )
        }

        override fun teardown() {
        }
    }

}