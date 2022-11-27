package dev.olog.image.provider.fetcher.internal

import com.bumptech.glide.Priority
import com.bumptech.glide.integration.okhttp3.OkHttpStreamFetcher
import com.bumptech.glide.load.model.GlideUrl
import dev.olog.core.entity.ImageRetrieverResult
import dev.olog.image.provider.internal.ImageLoaderPreferences
import dev.olog.image.provider.internal.NetworkCallLimiter
import dev.olog.image.provider.internal.loadDataSuspend
import okhttp3.OkHttpClient
import java.io.InputStream

internal abstract class RemoteFetcher<T : Any>(
    private val prefs: ImageLoaderPreferences,
    private val limiter: NetworkCallLimiter,
    private val client: OkHttpClient,
) {

    suspend fun load(priority: Priority, model: T): ImageRetrieverResult<InputStream> {
        if (!prefs.canDownloadImages()) {
            return ImageRetrieverResult.Error(IllegalStateException("not permitted"))
        }

        val result = limiter.execute { getRemote(model) }
        return when (result) {
            is ImageRetrieverResult.Success -> {
                val stream = OkHttpStreamFetcher(client, GlideUrl(result.data)).loadDataSuspend(priority)
                ImageRetrieverResult(stream)
            }
            is ImageRetrieverResult.NotFound -> ImageRetrieverResult.NotFound
            is ImageRetrieverResult.Error -> ImageRetrieverResult.Error(result.exception)
        }
    }

    protected abstract suspend fun getRemote(model: T): ImageRetrieverResult<String>

}

