package dev.olog.image.provider.fetcher

import android.content.Context
import androidx.preference.PreferenceManager
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.data.HttpUrlFetcher
import com.bumptech.glide.load.model.GlideUrl
import dev.olog.image.provider.R
import dev.olog.image.provider.executor.GlideScope
import dev.olog.shared.utils.NetworkUtils
import kotlinx.coroutines.*
import java.io.InputStream
import java.util.concurrent.atomic.AtomicLong

/**
 * Class that uses a 'global' counter to delay the image request for an image.
 * Because LastFm allows 5 request per second for user
 */
abstract class BaseDataFetcher(
    private val context: Context

) : DataFetcher<InputStream>, CoroutineScope by GlideScope() {

    companion object {
        private const val TIMEOUT = 2500

        private var requestCounter = AtomicLong(1)
    }

    private val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)

    private var hasIncremented = false
    private var hasDecremented = false

    override fun getDataClass(): Class<InputStream> = InputStream::class.java

    override fun getDataSource(): DataSource = DataSource.LOCAL

    override fun cleanup() {
        unsubscribe()
    }

    override fun cancel() {
        unsubscribe()
    }

    private fun unsubscribe() {
        cancel(null)
        if (hasIncremented && !hasDecremented) {
            requestCounter.decrementAndGet()
        }
    }

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        launch {
            try {
                if (mustFetch()) {
                    delayRequest()
                    yield()
                }
                val image = execute(priority, callback)
                yield()

                if (image.isNotBlank() && networkSafeAction()){
                    val urlFetcher = HttpUrlFetcher(
                        GlideUrl(image),
                        TIMEOUT
                    )
                    urlFetcher.loadData(priority, callback)
                    return@launch
                }
                callback.onLoadFailed(NoSuchElementException())
            } catch (ex: Exception){
                ex.printStackTrace()
                callback.onLoadFailed(ex)
            }
        }
    }

    private suspend fun delayRequest() {
        val current = requestCounter.incrementAndGet()
        hasIncremented = true
        delay(current * threshold)
        requestCounter.decrementAndGet()
        hasDecremented = true
    }

    private fun networkSafeAction(): Boolean {

        val downloadMode = prefs.getString(
            context.getString(R.string.prefs_auto_download_images_key),
            context.getString(R.string.prefs_auto_download_images_entry_value_wifi)
        )

        val isWifiActive = NetworkUtils.isOnWiFi(context)

        return when (downloadMode) {
            context.getString(R.string.prefs_auto_download_images_entry_value_never) -> false
            context.getString(R.string.prefs_auto_download_images_entry_value_wifi) -> isWifiActive
            context.getString(R.string.prefs_auto_download_images_entry_value_always) -> true
            else -> throw IllegalArgumentException("not supposed to happen, key not valid=$downloadMode")
        }
    }

    protected abstract suspend fun execute(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>): String

    protected abstract suspend fun mustFetch(): Boolean

    protected abstract val threshold: Long

}