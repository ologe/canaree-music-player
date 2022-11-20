package dev.olog.image.provider.loading

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import dev.olog.core.MediaId
import dev.olog.image.provider.CoverUtils
import dev.olog.image.provider.GlideApp
import dev.olog.image.provider.GlideRequest
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun Context.getCachedBitmap(
    mediaId: MediaId,
    imageSize: ImageSize = ImageSize.Large,
    gradientOnly: Boolean = false,
    onlyRetrieveFromCache: Boolean = true,
    priority: Priority = Priority.Immediate,
    extension: (GlideRequest<Bitmap>.() -> GlideRequest<Bitmap>) = { this },
): Bitmap? {
    return getCachedBitmap(
        model = mediaId,
        imageSize = imageSize,
        onlyRetrieveFromCache = onlyRetrieveFromCache,
        priority = priority,
        extension = extension
    ) ?: getCachedBitmap(
        model = if (gradientOnly) CoverUtils.onlyGradient(this, mediaId) else CoverUtils.getGradient(this, mediaId),
        imageSize = imageSize,
        onlyRetrieveFromCache = false,
        priority = priority,
        extension = extension
    )
}

suspend fun Context.getCachedBitmap(
    model: Any?,
    imageSize: ImageSize = ImageSize.Large,
    onlyRetrieveFromCache: Boolean = model !is Drawable,
    priority: Priority = Priority.Immediate,
    extension: (GlideRequest<Bitmap>.() -> GlideRequest<Bitmap>) = { this },
): Bitmap? = suspendCoroutine { continuation ->

    GlideApp.with(this)
        .asBitmap()
        .load(model)
        .override(imageSize.size)
        .priority(priority.toGlidePriority())
        .extension()
        .onlyRetrieveFromCache(onlyRetrieveFromCache)
        .into(object : CustomTarget<Bitmap>() {

            override fun onLoadCleared(placeholder: Drawable?) {

            }

            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                continuation.resume(resource)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                continuation.resume(null)
            }
        })
}