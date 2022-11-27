package dev.olog.image.provider.fetcher.internal

import dev.olog.core.entity.ImageRetrieverResult
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.ImageRetrieverGateway
import dev.olog.image.provider.fetcher.internal.RemoteFetcher
import dev.olog.image.provider.internal.ImageLoaderPreferences
import dev.olog.image.provider.internal.NetworkCallLimiter
import okhttp3.OkHttpClient
import javax.inject.Inject

internal class RemoteSongFetcher @Inject constructor(
    private val imageRetrieverGateway: ImageRetrieverGateway,
    prefs: ImageLoaderPreferences,
    limiter: NetworkCallLimiter,
    okHttpClient: OkHttpClient,
) : RemoteFetcher<Song>(prefs, limiter, okHttpClient) {

    override suspend fun getRemote(model: Song): ImageRetrieverResult<String> {
        return imageRetrieverGateway.fetchSongImage(model.id)
    }
}