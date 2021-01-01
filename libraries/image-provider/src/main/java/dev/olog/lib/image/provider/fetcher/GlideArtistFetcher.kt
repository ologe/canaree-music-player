package dev.olog.lib.image.provider.fetcher

import android.content.Context
import com.bumptech.glide.Priority
import com.bumptech.glide.load.data.DataFetcher
import dev.olog.core.mediaid.MediaId
import dev.olog.core.gateway.ImageRetrieverGateway
import java.io.InputStream

// for some reason last fm for some artists (maybe all) is returning a start instead of the artist image, this
// is the name of the image
private const val LAST_FM_PLACEHOLDER = "2a96cbd8b46e442fc41c2b86b821562f.png"

private const val DEEZER_PLACEHOLDER = "https://cdns-images.dzcdn.net/images/artist//"

class GlideArtistFetcher(
    context: Context,
    mediaId: MediaId,
    private val imageRetrieverGateway: ImageRetrieverGateway

) : BaseDataFetcher(context) {

    private val id = mediaId.resolveId

    override suspend fun execute(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>): String {
        val image = imageRetrieverGateway.getArtist(id)!!.image
        if (image.endsWith(LAST_FM_PLACEHOLDER) || image.startsWith(DEEZER_PLACEHOLDER)) {
            return ""
        }
        return image
    }

    override suspend fun mustFetch(): Boolean {
        return imageRetrieverGateway.mustFetchArtist(id)
    }

    override val threshold: Long = 250
}