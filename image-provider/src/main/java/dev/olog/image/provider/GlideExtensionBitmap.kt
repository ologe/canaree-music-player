package dev.olog.image.provider

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Priority
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import dev.olog.domain.MediaId
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

sealed class OnImageLoadingError {
    class Placeholder(val gradientOnly: Boolean) : OnImageLoadingError()
    object None : OnImageLoadingError()
}

suspend fun Context.getBitmap(
    mediaId: MediaId,
    size: Int = GlideUtils.OVERRIDE_BIG,
    extension: (GlideRequest<Bitmap>.() -> GlideRequest<Bitmap>)? = null,
    onError: OnImageLoadingError = OnImageLoadingError.Placeholder(false)
): Bitmap? {
    return getBitmapInternal(
        mediaId = mediaId,
        size = size,
        onlyFromCache = false,
        extension = extension,
        onError = onError
    )
}

suspend fun Context.getCachedBitmap(
    mediaId: MediaId,
    size: Int = GlideUtils.OVERRIDE_BIG,
    extension: (GlideRequest<Bitmap>.() -> GlideRequest<Bitmap>)? = null,
    onError: OnImageLoadingError = OnImageLoadingError.Placeholder(false)
): Bitmap?  {
    return getBitmapInternal(
        mediaId = mediaId,
        size = size,
        onlyFromCache = true,
        extension = extension,
        onError = onError
    )
}

private suspend fun Context.getBitmapInternal(
    mediaId: MediaId,
    size: Int = GlideUtils.OVERRIDE_BIG,
    onlyFromCache: Boolean,
    extension: (GlideRequest<Bitmap>.() -> GlideRequest<Bitmap>)? = null,
    onError: OnImageLoadingError = OnImageLoadingError.Placeholder(false)
): Bitmap? = suspendCancellableCoroutine { continuation ->

    GlideApp.with(this)
        .asBitmap()
        .load(mediaId)
        .override(size)
        .priority(Priority.IMMEDIATE)
        .extend(extension)
        .onlyRetrieveFromCache(onlyFromCache)
        .into(CachedImageLoaderTarget(continuation, this, mediaId, size, extension, onError))
}

private class CachedImageLoaderTarget(
    private val continuation: CancellableContinuation<Bitmap?>,
    private val context: Context,
    private val mediaId: MediaId,
    private val size: Int,
    private val extension: (GlideRequest<Bitmap>.() -> GlideRequest<Bitmap>)? = null,
    private val onError: OnImageLoadingError
) : CustomTarget<Bitmap>() {

    override fun onLoadCleared(placeholder: Drawable?) {
        if (continuation.isActive) {
            continuation.resume(null)
        }
    }

    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
        if (continuation.isActive) {
            continuation.resume(resource)
        }
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        when (onError) {
            is OnImageLoadingError.Placeholder -> buildPlaceholderLoader(onError)
            is OnImageLoadingError.None -> {
                if (continuation.isActive) {
                    continuation.resume(null)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        continuation.cancel()
    }

    private fun buildPlaceholderLoader(onError: OnImageLoadingError.Placeholder) {
        val placeholder: Drawable = if (onError.gradientOnly) {
            CoverUtils.onlyGradient(context, mediaId)
        } else {
            CoverUtils.getGradient(context, mediaId)
        }
        val bestSize = calculateBestSize(placeholder, size)

        GlideApp.with(context)
            .asBitmap()
            .load(placeholder.toBitmap(bestSize, bestSize))
            .extend(extension)
            .into(PlaceholderLoader(continuation))
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

}

private class PlaceholderLoader(
    private val continuation: CancellableContinuation<Bitmap?>
) : CustomTarget<Bitmap>() {

    override fun onLoadCleared(placeholder: Drawable?) {
        if (continuation.isActive) {
            continuation.resume(null)
        }
    }

    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
        if (continuation.isActive) {
            continuation.resume(resource)
        }
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        if (continuation.isActive) {
            continuation.resume(null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        continuation.cancel()
    }
}

internal fun GlideRequest<Bitmap>.extend(func: (GlideRequest<Bitmap>.() -> GlideRequest<Bitmap>)?): GlideRequest<Bitmap> {
    if (func != null) {
        return this.func()
    }
    return this
}