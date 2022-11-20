package dev.olog.image.provider.fetcher.internal

import dev.olog.core.MediaId
import dev.olog.core.entity.ImageRetrieverResult
import dev.olog.core.gateway.ImageRetrieverGateway
import dev.olog.image.provider.fetcher.internal.RemoteFetcher
import dev.olog.image.provider.internal.ImageLoaderPreferences
import dev.olog.image.provider.internal.NetworkCallLimiter
import okhttp3.OkHttpClient
import javax.inject.Inject

internal class RemoteArtistFetcher @Inject constructor(
    private val imageRetrieverGateway: ImageRetrieverGateway,
    prefs: ImageLoaderPreferences,
    limiter: NetworkCallLimiter,
    okHttpClient: OkHttpClient,
) : RemoteFetcher<MediaId>(prefs, limiter, okHttpClient) {

    override suspend fun getRemote(model: MediaId): ImageRetrieverResult<String> {
        return imageRetrieverGateway.fetchArtistImage(model.categoryId)
    }

}