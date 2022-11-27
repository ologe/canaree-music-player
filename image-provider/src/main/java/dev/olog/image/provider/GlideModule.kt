package dev.olog.image.provider

import android.app.ActivityManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.util.Log
import androidx.annotation.Keep
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.Excludes
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpLibraryGlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.executor.GlideExecutor
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import dagger.hilt.EntryPoints
import dev.olog.core.MediaId
import dev.olog.image.provider.animation.RemoteOnlyTransitionFactory
import dev.olog.image.provider.decoder.LayerDrawableBitmapDecoder
import dev.olog.image.provider.di.ImageProviderComponent
import dev.olog.image.provider.internal.CanareeUncaughtThrowableStrategy
import java.io.InputStream

@Keep
@GlideModule
@Excludes(OkHttpLibraryGlideModule::class)
class GlideModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val component = component(context)
        val strategy = CanareeUncaughtThrowableStrategy()

        builder.setLogLevel(Log.ERROR)
            .setDefaultRequestOptions(defaultRequestOptions(context))
            .setDiskCacheExecutor(GlideExecutor.newDiskCacheBuilder().setUncaughtThrowableStrategy(strategy).build())
            // merged images creation can block other load requests
            .setSourceExecutor(GlideExecutor.newUnlimitedSourceExecutor())
            .setAnimationExecutor(GlideExecutor.newAnimationBuilder().setUncaughtThrowableStrategy(strategy).build())
            .useLifecycleInsteadOfInjectingFragments(true)
            .setDefaultTransitionOptions(
                Drawable::class.java,
                DrawableTransitionOptions.with(RemoteOnlyTransitionFactory())
            ).setDiskCache { component.diskCache() }
    }

    private fun defaultRequestOptions(context: Context): RequestOptions {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        return RequestOptions()
            // Prefer higher quality images unless we're on a low RAM device
            .format(
                if (activityManager.isLowRamDevice) DecodeFormat.PREFER_RGB_565 else DecodeFormat.PREFER_ARGB_8888
            )
            .disallowHardwareConfig()
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .centerCrop()
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val component = component(context)

        registry.append(MediaId::class.java, InputStream::class.java, component.mediaIdFactory())
        registry.append(LayerDrawable::class.java, Bitmap::class.java, LayerDrawableBitmapDecoder(glide.bitmapPool))

        // custom implementation of OkHttpLibraryGlideModule to reuse existing existing okhttp
        registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(component.okHttpClient()));
    }

    override fun isManifestParsingEnabled(): Boolean = false

    private fun component(context: Context): ImageProviderComponent {
        return EntryPoints.get(context, ImageProviderComponent::class.java)
    }

}