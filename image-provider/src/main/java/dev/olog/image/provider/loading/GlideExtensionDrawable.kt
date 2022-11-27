package dev.olog.image.provider.loading

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import dev.olog.core.MediaId
import dev.olog.image.provider.CoverUtils
import dev.olog.image.provider.GlideApp
import dev.olog.image.provider.GlideRequest
import kotlinx.coroutines.suspendCancellableCoroutine

suspend fun Context.loadImage(
    mediaId: MediaId,
    imageSize: ImageSize,
    loadError: LoadErrorStrategy,
    priority: Priority = Priority.Normal,
    extension: (GlideRequest<Bitmap>.() -> GlideRequest<Bitmap>) = { this },
    onlyRetrieveFromCache: Boolean = false,
): Bitmap? {
    val result = loadImageInternal(
        load = mediaId,
        imageSize = imageSize,
        priority = priority,
        extension = extension,
        onlyRetrieveFromCache = onlyRetrieveFromCache,
    )
    if (result != null) {
        return result
    }

    return when (loadError) {
        LoadErrorStrategy.Full -> loadFallbackDrawable(
            load = CoverUtils.full(this, mediaId),
            imageSize = imageSize,
            extension = extension,
        )
        LoadErrorStrategy.Gradient -> loadFallbackDrawable(
            load = CoverUtils.onlyGradient(this, mediaId),
            imageSize = imageSize,
            extension = extension,
        )
        LoadErrorStrategy.None -> null
    }
}

private suspend fun Context.loadImageInternal(
    load: Any,
    imageSize: ImageSize,
    priority: Priority = Priority.Normal,
    extension: (GlideRequest<Bitmap>.() -> GlideRequest<Bitmap>) = { this },
    onlyRetrieveFromCache: Boolean = false,
) = suspendCancellableCoroutine { continuation ->
    GlideApp.with(this)
        .asBitmap()
        .load(load)
        .override(imageSize.size)
        .priority(priority.toGlidePriority())
        .extension()
        .onlyRetrieveFromCache(onlyRetrieveFromCache)
        .into(CoroutinesCustomTarget(imageSize.size, imageSize.size, continuation))
}

private suspend fun Context.loadFallbackDrawable(
    load: Drawable,
    imageSize: ImageSize,
    extension: (GlideRequest<Bitmap>.() -> GlideRequest<Bitmap>) = { this },
) = suspendCancellableCoroutine { continuation ->
    GlideApp.with(this)
        .asBitmap()
        .load(load)
        .override(imageSize.size)
        .priority(com.bumptech.glide.Priority.IMMEDIATE)
        .extension()
        .into(CoroutinesCustomTarget(imageSize.size, imageSize.size, continuation))
}