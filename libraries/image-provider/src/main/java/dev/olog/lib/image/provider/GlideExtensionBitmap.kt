package dev.olog.lib.image.provider

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Priority
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import dev.olog.core.MediaId
import dev.olog.shared.safeResume
import kotlin.coroutines.suspendCoroutine

sealed class OnImageLoadingError {
    class Placeholder(val gradientOnly: Boolean) : OnImageLoadingError()
    object None : OnImageLoadingError()
}

suspend fun Context.getCachedBitmap(
    mediaId: MediaId,
    size: Int = GlideUtils.OVERRIDE_BIG,
    extension: (GlideRequest<Bitmap>.() -> GlideRequest<Bitmap>)? = null,
    onError: OnImageLoadingError = OnImageLoadingError.Placeholder(false)
): Bitmap? = suspendCoroutine { continuation ->


    GlideApp.with(this)
        .asBitmap()
        .load(mediaId)
        .override(size)
        .priority(Priority.IMMEDIATE)
        .extend(extension)
        .onlyRetrieveFromCache(true)
        .into(object : CustomTarget<Bitmap>() {

            override fun onLoadCleared(placeholder: Drawable?) {
                continuation.safeResume(null)
            }

            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                continuation.safeResume(resource)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                if (onError is OnImageLoadingError.Placeholder) {
                    val placeholder: Drawable = if (onError.gradientOnly) {
                        CoverUtils.onlyGradient(this@getCachedBitmap, mediaId)
                    } else {
                        CoverUtils.getGradient(this@getCachedBitmap, mediaId)
                    }
                    val bestSize = calculateBestSize(placeholder, size)

                    GlideApp.with(this@getCachedBitmap)
                        .asBitmap()
                        .load(placeholder.toBitmap(bestSize, bestSize))
                        .extend(extension)
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                continuation.safeResume(resource)
                            }

                            override fun onLoadFailed(errorDrawable: Drawable?) {
                                continuation.safeResume(null)
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                                try {
                                    continuation.safeResume(null)
                                } catch (ex: Throwable){
                                    // already resumed
                                }
                            }
                        })

                } else {
                    continuation.safeResume(null)
                }
            }
        })
}

private fun calculateBestSize(drawable: Drawable, requestedSize: Int): Int {
    if (requestedSize != GlideUtils.OVERRIDE_BIG){
        return requestedSize
    }

    if (drawable.intrinsicHeight > 0 && drawable.intrinsicHeight > 0){
        return drawable.intrinsicHeight
    }
    return 300 // random size
}

internal fun GlideRequest<Bitmap>.extend(func: (GlideRequest<Bitmap>.() -> GlideRequest<Bitmap>)?): GlideRequest<Bitmap> {
    if (func != null) {
        return this.func()
    }
    return this
}