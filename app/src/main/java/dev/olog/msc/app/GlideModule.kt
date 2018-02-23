package dev.olog.msc.app

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions

@GlideModule
class GlideModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {

        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        val defaultOptions = RequestOptions()
                // Prefer higher quality images unless we're on a low RAM device
                .format(if (activityManager.isLowRamDevice)
                    DecodeFormat.PREFER_RGB_565 else DecodeFormat.PREFER_ARGB_8888
                ).disallowHardwareConfig()

        builder.setLogLevel(Log.ERROR)
                .setDefaultRequestOptions(defaultOptions)
                .build(context)
    }

    override fun isManifestParsingEnabled(): Boolean = false

}
