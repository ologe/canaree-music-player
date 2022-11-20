package dev.olog.image.provider.loading

import android.content.Context
import android.graphics.drawable.Drawable
import com.bumptech.glide.Priority
import dev.olog.image.provider.GlideApp
import kotlin.coroutines.suspendCoroutine

/**
 * Returns only the requested image, no placeholders
 */
suspend fun Context.getDrawable(
    mediaId: Any?,
    imageSize: ImageSize,
    priority: Priority = Priority.HIGH
): Drawable? = suspendCoroutine { continuation ->
    GlideApp.with(this)
        .load(mediaId)
        .override(imageSize.size)
        .priority(priority)
        .listener(CoroutinesDrawableRequestListener(continuation))
        .into(CoroutinesDrawableCustomTarget(imageSize.size, imageSize.size))
}