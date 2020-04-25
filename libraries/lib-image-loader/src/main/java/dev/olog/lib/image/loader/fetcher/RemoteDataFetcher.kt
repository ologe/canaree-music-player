package dev.olog.lib.image.loader.fetcher

import android.content.Context
import com.bumptech.glide.Priority
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.data.HttpUrlFetcher
import com.bumptech.glide.load.model.GlideUrl
import dev.olog.domain.prefs.AppPreferencesGateway
import dev.olog.domain.schedulers.Schedulers
import dev.olog.lib.image.loader.R
import dev.olog.shared.android.utils.NetworkUtils
import dev.olog.shared.coroutines.DispatcherScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.InputStream
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

/**
 * Class that uses a 'global' counter to delay the image request for an image.
 * Because LastFm allows 5 request per second for user
 */
class RemoteDataFetcher(
    private val context: Context,
    private val prefsGateway: AppPreferencesGateway,
    schedulers: Schedulers,
    private val priority: Priority,
    private val callback: DataFetcher.DataCallback<in InputStream>,
    private val threshold: Long,
    private val getCached: suspend () -> String?,
    private val getRemote: suspend () -> String?
) {

    companion object {
        private val TIMEOUT = TimeUnit.SECONDS.toMillis(10L).toInt()
        @JvmStatic
        private var requestCounter = AtomicLong(1)
    }

    private val scope by DispatcherScope(schedulers.io)

    private var hasIncremented = false
    private var hasDecremented = false

    fun teardown() {
        scope.cancel()
        if (hasIncremented && !hasDecremented) {
            requestCounter.decrementAndGet()
        }
    }

    fun loadData() {
        scope.launch {
            if (tryLocal(priority, callback)) {
                return@launch
            }
            if (tryRemote(priority, callback)) {
                return@launch
            }
            callback.onDataReady(null)
        }
    }

    private suspend fun tryLocal(
        priority: Priority,
        callback: DataFetcher.DataCallback<in InputStream>
    ): Boolean {
        // load local
        val image = getCached()?.takeIf { it.isNotBlank() } ?: return false

        loadUrl(image, priority, callback)
        return true
    }

    private suspend fun tryRemote(
        priority: Priority,
        callback: DataFetcher.DataCallback<in InputStream>
    ): Boolean {
        if (!networkSafeAction()) {
            return false
        }
        // delay
        delayRequest()

        // rest call to last fm
        val image = getRemote()?.takeIf { it.isNotBlank() } ?: return false

        loadUrl(image, priority, callback)
        return true
    }

    private fun loadUrl(
        url: String,
        priority: Priority,
        callback: DataFetcher.DataCallback<in InputStream>
    ) {
        val urlFetcher = HttpUrlFetcher(GlideUrl(url), TIMEOUT)
        urlFetcher.loadData(priority, callback)
    }

    private suspend fun delayRequest() {
        val current = requestCounter.incrementAndGet()
        hasIncremented = true
        delay(current * threshold)
        requestCounter.decrementAndGet()
        hasDecremented = true
    }

    private fun networkSafeAction(): Boolean {
        // TODO made an enum??
        val downloadMode = prefsGateway.canAutoDownloadImages()

        return when (downloadMode) {
            context.getString(R.string.prefs_auto_download_images_entry_value_never) -> false
            context.getString(R.string.prefs_auto_download_images_entry_value_wifi) -> NetworkUtils.isOnWiFi(context)
            context.getString(R.string.prefs_auto_download_images_entry_value_always) -> true
            else -> throw IllegalArgumentException("not supposed to happen, key not valid=$downloadMode")
        }
    }

}