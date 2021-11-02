package dev.olog.image.provider

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import androidx.annotation.Keep
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.executor.GlideExecutor
import com.bumptech.glide.load.engine.executor.GlideExecutor.UncaughtThrowableStrategy.DEFAULT
import com.bumptech.glide.load.engine.executor.GlideExecutor.UncaughtThrowableStrategy.IGNORE
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.core.Config
import dev.olog.core.MediaId
import dev.olog.image.provider.loader.AudioFileCoverLoader
import dev.olog.image.provider.loader.GlideImageRetrieverLoader
import dev.olog.image.provider.loader.GlideMergedImageLoader
import dev.olog.image.provider.loader.GlideOriginalImageLoader
import dev.olog.image.provider.model.AudioFileCover
import java.io.InputStream

@GlideModule
@Keep
internal class GlideModule : AppGlideModule() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface GlideEntryPoint {
        fun lastFmFactory(): GlideImageRetrieverLoader.Factory
        fun originalFactory(): GlideOriginalImageLoader.Factory
        fun mergedFactory(): GlideMergedImageLoader.Factory
        fun config(): Config
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val component = EntryPoints.get(context.applicationContext, GlideEntryPoint::class.java)

        val level = if (component.config().isDebug) DEFAULT else IGNORE
        builder.setLogLevel(Log.ERROR)
            .setDefaultRequestOptions(defaultRequestOptions(context))
            .setDiskCacheExecutor(GlideExecutor.newDiskCacheExecutor(level))
            .setSourceExecutor(GlideExecutor.newSourceExecutor(level))
    }

    private fun defaultRequestOptions(context: Context): RequestOptions {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        return RequestOptions()
            // Prefer higher quality images unless we're on a low RAM device
            .format(
                if (activityManager.isLowRamDevice)
                    DecodeFormat.PREFER_RGB_565 else DecodeFormat.PREFER_ARGB_8888
            ).disallowHardwareConfig()
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .centerCrop()
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.prepend(AudioFileCover::class.java, InputStream::class.java, AudioFileCoverLoader.Factory())

        val component = EntryPoints.get(context.applicationContext, GlideEntryPoint::class.java)
        registry.prepend(MediaId::class.java, InputStream::class.java, component.lastFmFactory())
        registry.prepend(MediaId::class.java, InputStream::class.java, component.mergedFactory())
        registry.prepend(MediaId::class.java, InputStream::class.java, component.originalFactory())
    }

    override fun isManifestParsingEnabled(): Boolean = false

}