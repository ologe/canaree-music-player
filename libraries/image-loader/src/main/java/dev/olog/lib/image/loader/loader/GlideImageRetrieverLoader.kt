package dev.olog.lib.image.loader.loader

import android.content.Context
import android.content.SharedPreferences
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.domain.MediaId
import dev.olog.domain.MediaIdCategory.*
import dev.olog.domain.gateway.ImageRetrieverGateway
import dev.olog.domain.schedulers.Schedulers
import dev.olog.lib.image.loader.fetcher.GlideAlbumFetcher
import dev.olog.lib.image.loader.fetcher.GlideArtistFetcher
import dev.olog.lib.image.loader.fetcher.GlideSongFetcher
import java.io.InputStream
import javax.inject.Inject

private val allowedCategories = listOf(ALBUMS, ARTISTS)
private val spotifyCategories = listOf(SPOTIFY_TRACK, SPOTIFY_ALBUMS)

// TODO inject properly, don't fear dagger
internal class GlideImageRetrieverLoader(
    private val context: Context,
    private val imageRetrieverGateway: ImageRetrieverGateway,
    private val prefs: SharedPreferences,
    private val schedulers: Schedulers
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
                prefs = prefs,
                schedulers = schedulers
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
                prefs = prefs,
                schedulers = schedulers
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
                prefs = prefs,
                schedulers = schedulers
            )
        )
    }

    class Factory @Inject constructor(
        @ApplicationContext private val context: Context,
        private val imageRetrieverGateway: ImageRetrieverGateway,
        private val prefs: SharedPreferences,
        private val schedulers: Schedulers
    ) : ModelLoaderFactory<MediaId, InputStream> {

        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<MediaId, InputStream> {
            return GlideImageRetrieverLoader(
                context,
                imageRetrieverGateway,
                prefs,
                schedulers
            )
        }

        override fun teardown() {
        }
    }

}