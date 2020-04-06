package dev.olog.image.provider.loader

import android.content.Context
import android.content.SharedPreferences
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import dev.olog.domain.MediaId
import dev.olog.domain.MediaIdCategory.*
import dev.olog.domain.gateway.ImageRetrieverGateway
import dev.olog.image.provider.fetcher.GlideAlbumFetcher
import dev.olog.image.provider.fetcher.GlideArtistFetcher
import dev.olog.image.provider.fetcher.GlideSongFetcher
import dev.olog.core.ApplicationContext
import java.io.InputStream
import javax.inject.Inject

private val allowedCategories = listOf(ALBUMS, ARTISTS)
private val spotifyCategories = listOf(SPOTIFY_TRACK, SPOTIFY_ALBUMS)

internal class GlideImageRetrieverLoader(
    private val context: Context,
    private val imageRetrieverGateway: ImageRetrieverGateway,
    private val prefs: SharedPreferences
) : ModelLoader<MediaId, InputStream> {

    override fun handles(mediaId: MediaId): Boolean {
        if (mediaId.isAnyPodcast) {
            return false
        }
        return when (mediaId){
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

        return when (mediaId) {
            is MediaId.Track -> buildTrackLoader(mediaId)
            is MediaId.Category -> buildCategoryLoader(mediaId)
        }
    }

    private fun buildTrackLoader(mediaId: MediaId.Track): ModelLoader.LoadData<InputStream> {
        return ModelLoader.LoadData(
            MediaIdKey(mediaId),
            GlideSongFetcher(
                context = context,
                mediaId = mediaId,
                imageRetrieverGateway = imageRetrieverGateway,
                prefs = prefs
            )
        )
    }

    private fun buildCategoryLoader(mediaId: MediaId.Category): ModelLoader.LoadData<InputStream>? {
        return when (mediaId.category) {
            ALBUMS -> buildAlbumLoader(mediaId)
            ARTISTS -> buildArtistLoader(mediaId)
            else -> null
        }
    }

    private fun buildAlbumLoader(mediaId: MediaId.Category): ModelLoader.LoadData<InputStream> {
        return ModelLoader.LoadData(
            MediaIdKey(mediaId),
            GlideAlbumFetcher(
                context = context,
                mediaId = mediaId,
                imageRetrieverGateway = imageRetrieverGateway,
                prefs = prefs
            )
        )
    }

    private fun buildArtistLoader(mediaId: MediaId.Category): ModelLoader.LoadData<InputStream> {
        return ModelLoader.LoadData(
            MediaIdKey(mediaId),
            GlideArtistFetcher(
                context = context,
                mediaId = mediaId,
                imageRetrieverGateway = imageRetrieverGateway,
                prefs = prefs
            )
        )
    }

    class Factory @Inject constructor(
        @dev.olog.core.ApplicationContext private val context: Context,
        private val imageRetrieverGateway: ImageRetrieverGateway,
        private val prefs: SharedPreferences

    ) : ModelLoaderFactory<MediaId, InputStream> {

        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<MediaId, InputStream> {
            return GlideImageRetrieverLoader(
                context,
                imageRetrieverGateway,
                prefs
            )
        }

        override fun teardown() {
        }
    }

}