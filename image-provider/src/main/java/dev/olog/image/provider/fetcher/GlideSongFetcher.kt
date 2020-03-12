package dev.olog.image.provider.fetcher

import android.content.Context
import android.content.SharedPreferences
import dev.olog.core.MediaId
import dev.olog.core.gateway.ImageRetrieverGateway

class GlideSongFetcher(
    context: Context,
    private val mediaId: MediaId.Track,
    private val imageRetrieverGateway: ImageRetrieverGateway,
    prefs: SharedPreferences
) : BaseDataFetcher(context, prefs) {

    companion object {
        private const val THRESHOLD = 600L
    }

    override suspend fun execute(): String {
        return imageRetrieverGateway.getTrack(mediaId.id)!!.image
    }

    override suspend fun mustFetch(): Boolean {
        return imageRetrieverGateway.mustFetchTrack(mediaId.id)
    }

    override val threshold: Long = THRESHOLD
}