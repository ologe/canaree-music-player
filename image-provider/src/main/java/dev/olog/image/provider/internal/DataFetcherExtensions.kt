package dev.olog.image.provider.internal

import com.bumptech.glide.Priority
import com.bumptech.glide.load.data.DataFetcher
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.InputStream
import kotlin.coroutines.resume

internal suspend fun DataFetcher<InputStream>.loadDataSuspend(priority: Priority): InputStream? {
    return suspendCancellableCoroutine { continuation ->
        loadData(priority, object : DataFetcher.DataCallback<InputStream> {
            override fun onDataReady(data: InputStream?) {
                continuation.resume(data)
            }

            override fun onLoadFailed(e: Exception) {
                e.printStackTrace()
                continuation.resume(null)
            }
        })
    }
}