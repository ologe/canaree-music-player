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
import dagger.hilt.EntryPoints
import dev.olog.core.MediaId
import dev.olog.image.provider.di.ImageProviderComponent
import dev.olog.image.provider.loader.AudioFileCoverLoader
import dev.olog.image.provider.model.AudioFileCover
import java.io.InputStream

@GlideModule
@Keep
class GlideModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val component = component(context)
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
        val component = component(context)

        registry.prepend(AudioFileCover::class.java, InputStream::class.java, AudioFileCoverLoader.Factory())

        registry.prepend(MediaId::class.java, InputStream::class.java, component.lastFmFactory())
        registry.prepend(MediaId::class.java, InputStream::class.java, component.mergedFactory())
        registry.prepend(MediaId::class.java, InputStream::class.java, component.originalFactory())
    }

    override fun isManifestParsingEnabled(): Boolean = false

    private fun component(context: Context): ImageProviderComponent {
        return EntryPoints.get(context, ImageProviderComponent::class.java)
    }

}