package dev.olog.msc.app

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import dev.olog.msc.glide.GlideImageLoader
import dev.olog.msc.presentation.model.DisplayableItem
import java.io.InputStream

@GlideModule
class GlideModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {

        builder.setLogLevel(Log.ERROR)
                .setDefaultRequestOptions(defaultRequestOptions(context))
//                .setDiskCacheExecutor(GlideExecutor.newDiskCacheExecutor(IGNORE))
//                .setSourceExecutor(GlideExecutor.newSourceExecutor(IGNORE))
                .build(context)
    }

    private fun defaultRequestOptions(context: Context): RequestOptions {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        return RequestOptions()
                // Prefer higher quality images unless we're on a low RAM device
                .format(if (activityManager.isLowRamDevice)
                    DecodeFormat.PREFER_RGB_565 else DecodeFormat.PREFER_ARGB_8888
                ).disallowHardwareConfig()
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val lastFmGateway = (context.applicationContext as App).lastFmGateway
        val factory = GlideImageLoader.Factory(context, lastFmGateway)
        registry.prepend(DisplayableItem::class.java, InputStream::class.java, factory)
    }

    override fun isManifestParsingEnabled(): Boolean = false

}