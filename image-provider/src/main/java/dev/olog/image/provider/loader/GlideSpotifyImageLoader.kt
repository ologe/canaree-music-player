package dev.olog.image.provider.loader

import android.net.Uri
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.gateway.spotify.SpotifyGateway
import dev.olog.shared.android.utils.assertBackgroundThread
import java.io.InputStream
import javax.inject.Inject

private val allowedCategories = listOf(MediaIdCategory.SPOTIFY_ALBUMS)

internal class GlideSpotifyImageLoader(
    private val spotifyGateway: SpotifyGateway,
    private val uriLoader: ModelLoader<Uri, InputStream>
) : ModelLoader<MediaId.Category, InputStream> {

    override fun handles(mediaId: MediaId.Category): Boolean {
        return mediaId.category in allowedCategories
    }

    override fun buildLoadData(
        mediaId: MediaId.Category,
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
    ) : ModelLoaderFactory<MediaId.Category, InputStream> {

        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<MediaId.Category, InputStream> {
            val uriLoader = multiFactory.build(Uri::class.java, InputStream::class.java)
            return GlideSpotifyImageLoader(spotifyGateway, uriLoader)
        }

        override fun teardown() {
        }
    }

}