package dev.olog.image.provider.fetcher

import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import java.io.File
import java.io.InputStream

internal class GlideOverridenImageFetcher(
    private val overrideImage: String?
) : DataFetcher<InputStream> {

    override fun getDataClass(): Class<InputStream> = InputStream::class.java

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        if (overrideImage != null) {
            val file = File(overrideImage)
            if (file.exists()) {
                callback.onDataReady(file.inputStream())
                return
            }
        }
        callback.onLoadFailed(Exception("no override image"))
    }

    override fun cleanup() {

    }

    override fun getDataSource(): DataSource = DataSource.LOCAL

    override fun cancel() {

    }
}