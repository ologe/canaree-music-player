package dev.olog.lib.image.loader.fetcher

import android.content.Context
import android.content.SharedPreferences
import dev.olog.domain.MediaId
import dev.olog.domain.gateway.ImageRetrieverGateway
import dev.olog.domain.schedulers.Schedulers

class GlideSongFetcher(
    context: Context,
    private val mediaId: MediaId.Track,
    private val imageRetrieverGateway: ImageRetrieverGateway,
    prefs: SharedPreferences,
    schedulers: Schedulers
) : BaseDataFetcher(context, prefs, schedulers) {

    companion object {
        private const val THRESHOLD = 600L
    }

    override suspend fun execute(): String {
        return imageRetrieverGateway.getTrack(mediaId.id.toLong())!!.image
    }

    override suspend fun mustFetch(): Boolean {
        return imageRetrieverGateway.mustFetchTrack(mediaId.id.toLong())
    }

    override val threshold: Long = THRESHOLD
}