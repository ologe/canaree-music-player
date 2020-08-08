package dev.olog.lib.image.loader

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
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dev.olog.domain.MediaId
import dev.olog.lib.image.loader.loader.GlideImageRetrieverLoader
import dev.olog.lib.image.loader.loader.GlideMergedImageLoader
import dev.olog.lib.image.loader.loader.GlideOriginalImageLoader
import dev.olog.lib.image.loader.loader.GlideSpotifyImageLoader
import java.io.InputStream

@GlideModule
@Keep
internal class GlideModule : AppGlideModule() {

    @EntryPoint
    @InstallIn(ApplicationComponent::class)
    interface GlideEntryPoint {

        fun lastFmFactory(): GlideImageRetrieverLoader.Factory
        fun originalFactory(): GlideOriginalImageLoader.Factory
        fun mergedFactory(): GlideMergedImageLoader.Factory
        fun spotifyFactory(): GlideSpotifyImageLoader.Factory

    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setLogLevel(Log.ERROR)
            .setDefaultRequestOptions(defaultRequestOptions(context))
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
        val component = EntryPoints.get(context, GlideEntryPoint::class.java)

        registry.prepend(MediaId::class.java, InputStream::class.java, component.lastFmFactory())
        registry.prepend(MediaId.Category::class.java, InputStream::class.java, component.mergedFactory())
        registry.prepend(MediaId::class.java, InputStream::class.java, component.originalFactory())
        registry.prepend(MediaId::class.java, InputStream::class.java, component.spotifyFactory())
    }

    override fun isManifestParsingEnabled(): Boolean = false

}