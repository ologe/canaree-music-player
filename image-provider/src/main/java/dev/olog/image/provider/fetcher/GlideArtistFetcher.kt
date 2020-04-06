package dev.olog.image.provider.fetcher

import android.content.Context
import android.content.SharedPreferences
import dev.olog.domain.MediaId
import dev.olog.domain.gateway.ImageRetrieverGateway
import dev.olog.domain.schedulers.Schedulers

class GlideArtistFetcher(
    context: Context,
    private val mediaId: MediaId.Category,
    private val imageRetrieverGateway: ImageRetrieverGateway,
    prefs: SharedPreferences,
    schedulers: Schedulers
) : BaseDataFetcher(context, prefs, schedulers) {

    companion object {
        // for some reason last fm for some artists (maybe all) is returning a start instead of
        // the artist image, this is the name of the image
        internal const val LAST_FM_PLACEHOLDER = "2a96cbd8b46e442fc41c2b86b821562f.png"
        internal const val DEEZER_PLACEHOLDER = "https://cdns-images.dzcdn.net/images/artist//"
        private const val THRESHOLD = 250L
    }

    override suspend fun execute(): String {
        val image = imageRetrieverGateway.getArtist(mediaId.categoryId.toLong())!!.image
        if (image.endsWith(LAST_FM_PLACEHOLDER) || image.startsWith(DEEZER_PLACEHOLDER)) {
            return ""
        }
        return image
    }

    override suspend fun mustFetch(): Boolean {
        return imageRetrieverGateway.mustFetchArtist(mediaId.categoryId.toLong())
    }

    override val threshold: Long = THRESHOLD
}