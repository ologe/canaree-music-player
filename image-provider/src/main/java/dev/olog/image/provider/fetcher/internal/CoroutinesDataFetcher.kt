package dev.olog.image.provider.fetcher.internal

import androidx.annotation.CallSuper
import com.bumptech.glide.Priority
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.data.DataFetcher
import dev.olog.core.entity.ImageRetrieverResult
import dev.olog.image.provider.internal.DummyInputStream
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import java.io.InputStream

internal abstract class CoroutinesDataFetcher : DataFetcher<InputStream> {

    private var job: Job? = null

    final override fun loadData(
        priority: Priority,
        callback: DataFetcher.DataCallback<in InputStream>
    ) {
        dispose() // should not be needed

        job = Job().also {
            // runBlocking throws CancellationException when job is cancelled
            try {
                runBlocking(it) {
                    when (val result = load(priority)) {
                        is ImageRetrieverResult.Success -> callback.onDataReady(result.data)
                        is ImageRetrieverResult.NotFound -> callback.onDataReady(DummyInputStream())
                        is ImageRetrieverResult.Error -> callback.onLoadFailed(Exception(result.exception))
                    }
                }
            } catch (ex: Throwable) {
                ex.printStackTrace()
                // catch unhandled exceptions.
                // coroutines CancellationException is ignored in CanareeUncaughtThrowableStrategy
                callback.onLoadFailed(Exception(ex))
            }
        }
    }

    protected abstract suspend fun load(priority: Priority): ImageRetrieverResult<InputStream>

    @CallSuper
    override fun cancel() {
        dispose()
    }

    @CallSuper
    override fun cleanup() {
        dispose()
    }

    private fun dispose() {
        job?.cancel()
        job = null
    }

    abstract fun createCacheKey(): Key

}