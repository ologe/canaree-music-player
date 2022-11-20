package dev.olog.image.provider.loader

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory

internal class DrawableToBitmapLoader : ModelLoader<Drawable, Bitmap> {

    override fun buildLoadData(
        model: Drawable,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<Bitmap>? {
        return ModelLoader.LoadData(
            DrawableKey(model, width, height),
            Fetcher(
                drawable = model,
                width = width.takeIf { it > 0 } ?: model.intrinsicWidth,
                height = height.takeIf { it > 0 } ?: model.intrinsicHeight,
            )
        )
    }

    override fun handles(model: Drawable): Boolean = true

    private class Fetcher(
        private val drawable: Drawable,
        private val width: Int,
        private val height: Int,
    ) : DataFetcher<Bitmap> {

        override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in Bitmap>) {
            callback.onDataReady(drawable.toBitmap(width, height))
        }

        override fun cleanup() {
        }

        override fun cancel() {
        }

        override fun getDataClass(): Class<Bitmap> = Bitmap::class.java
        override fun getDataSource(): DataSource = DataSource.MEMORY_CACHE
    }

    class Factory : ModelLoaderFactory<Drawable, Bitmap> {

        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<Drawable, Bitmap> {
            return DrawableToBitmapLoader()
        }

        override fun teardown() {

        }
    }

}