package dev.olog.lib.image.loader.loader

import android.content.Context
import android.net.Uri
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import dev.olog.domain.MediaId
import dev.olog.domain.MediaIdCategory
import dev.olog.domain.gateway.ImageRetrieverGateway
import dev.olog.domain.gateway.spotify.SpotifyGateway
import dev.olog.domain.gateway.track.FolderGateway
import dev.olog.domain.gateway.track.GenreGateway
import dev.olog.domain.gateway.track.PlaylistGateway
import dev.olog.domain.gateway.track.TrackGateway
import dev.olog.domain.prefs.AppPreferencesGateway
import dev.olog.domain.schedulers.Schedulers
import dev.olog.lib.image.loader.fetcher.GlideDefaultFetcher
import dev.olog.lib.image.loader.fetcher.GlideMergedImageFetcher
import java.io.InputStream
import javax.inject.Inject

internal class GlideMediaIdLoaderDelegate(
    private val context: Context,
    private val uriLoader: ModelLoader<Uri, InputStream>,
    private val spotifyGateway: SpotifyGateway,
    private val prefsGateway: AppPreferencesGateway,
    private val trackGateway: TrackGateway,
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val genreGateway: GenreGateway,
    private val schedulers: Schedulers,
    private val imageRetrieverGateway: ImageRetrieverGateway

) : ModelLoader<MediaId, InputStream> {

    override fun buildLoadData(
        model: MediaId,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream>? {
        return when (model) {
            is MediaId.Category -> buildCategoryLoader(model, width, height, options)
            is MediaId.Track -> buildTrackLoader(model, width, height, options)
        }
    }

    private fun buildCategoryLoader(
        mediaId: MediaId.Category,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream>? {
        return when (mediaId.category) {
            MediaIdCategory.FOLDERS,
            MediaIdCategory.PLAYLISTS,
            MediaIdCategory.PODCASTS_PLAYLIST,
            MediaIdCategory.GENRES -> buildMergedFetcher(mediaId, width, height, options)
            MediaIdCategory.ALBUMS,
            MediaIdCategory.ARTISTS -> buildDefaultFetcher(mediaId)
            MediaIdCategory.SPOTIFY_ALBUMS -> buildSpotifyFetcher(mediaId, width, height, options)
            else -> null
        }
    }

    private fun buildTrackLoader(
        mediaId: MediaId.Track,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream>? {
        return when (mediaId.category) {
            MediaIdCategory.SPOTIFY_TRACK -> buildSpotifyFetcher(mediaId, width, height, options)
            else -> buildDefaultFetcher(mediaId)
        }
    }

    private fun buildMergedFetcher(
        mediaId: MediaId.Category,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream>? {
        if (!prefsGateway.canAutoCreateImages()) {
            return uriLoader.buildLoadData(Uri.EMPTY, width, height, options)
        } else {
            return ModelLoader.LoadData(
                MediaIdKey.create(mediaId),
                GlideMergedImageFetcher(
                    context = context,
                    mediaId = mediaId,
                    folderGateway = folderGateway,
                    playlistGateway = playlistGateway,
                    genreGateway = genreGateway,
                    schedulers = schedulers
                )
            )
        }
    }

    private fun buildDefaultFetcher(
        mediaId: MediaId
    ): ModelLoader.LoadData<InputStream>? {
        return ModelLoader.LoadData(
            MediaIdKey.create(mediaId),
            GlideDefaultFetcher(
                context = context,
                mediaId = mediaId,
                trackGateway = trackGateway,
                prefsGateway = prefsGateway,
                imageRetrieverGateway = imageRetrieverGateway,
                schedulers = schedulers
            )
        )
    }

    private fun buildSpotifyFetcher(
        mediaId: MediaId,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream>? {
        val image = spotifyGateway.getImage(mediaId.categoryId) ?: return null
        return uriLoader.buildLoadData(Uri.parse(image), width, height, options)
    }

    override fun handles(model: MediaId): Boolean = true

    class Factory @Inject constructor(
        private val context: Context,
        private val spotifyGateway: SpotifyGateway,
        private val prefsGateway: AppPreferencesGateway,
        private val trackGateway: TrackGateway,
        private val folderGateway: FolderGateway,
        private val playlistGateway: PlaylistGateway,
        private val genreGateway: GenreGateway,
        private val schedulers: Schedulers,
        private val imageRetrieverGateway: ImageRetrieverGateway
    ) : ModelLoaderFactory<MediaId, InputStream> {

        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<MediaId, InputStream> {
            val uriLoader = multiFactory.build(Uri::class.java, InputStream::class.java)
            return GlideMediaIdLoaderDelegate(
                context = context,
                uriLoader = uriLoader,
                spotifyGateway = spotifyGateway,
                prefsGateway = prefsGateway,
                trackGateway = trackGateway,
                folderGateway = folderGateway,
                playlistGateway = playlistGateway,
                genreGateway = genreGateway,
                schedulers = schedulers,
                imageRetrieverGateway = imageRetrieverGateway
            )
        }

        override fun teardown() {}
    }
}