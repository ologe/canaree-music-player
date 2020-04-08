package dev.olog.lib.image.loader.loader

import android.net.Uri
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import dev.olog.domain.MediaId
import dev.olog.domain.MediaIdCategory.SPOTIFY_ALBUMS
import dev.olog.domain.MediaIdCategory.SPOTIFY_TRACK
import dev.olog.domain.gateway.spotify.SpotifyGateway
import dev.olog.shared.android.utils.assertBackgroundThread
import java.io.InputStream
import javax.inject.Inject

private val allowedCategories = listOf(SPOTIFY_ALBUMS, SPOTIFY_TRACK)

internal class GlideSpotifyImageLoader(
    private val spotifyGateway: SpotifyGateway,
    private val uriLoader: ModelLoader<Uri, InputStream>
) : ModelLoader<MediaId, InputStream> {

    override fun handles(mediaId: MediaId): Boolean {
        return mediaId.category in allowedCategories
    }

    override fun buildLoadData(
        mediaId: MediaId,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream>? {
        assertBackgroundThread()
        val image = spotifyGateway.getImage(mediaId.categoryId) ?: return null
        return uriLoader.buildLoadData(Uri.parse(image), width, height, options)
    }

    class Factory @Inject constructor(
        private val spotifyGateway: SpotifyGateway
    ) : ModelLoaderFactory<MediaId, InputStream> {

        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<MediaId, InputStream> {
            val uriLoader = multiFactory.build(Uri::class.java, InputStream::class.java)
            return GlideSpotifyImageLoader(spotifyGateway, uriLoader)
        }

        override fun teardown() {
        }
    }

}