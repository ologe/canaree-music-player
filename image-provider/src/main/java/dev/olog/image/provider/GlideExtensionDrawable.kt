package dev.olog.image.provider

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Priority
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import dev.olog.core.MediaId
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun Context.getCachedDrawable(
    mediaId: MediaId,
    size: Int = Target.SIZE_ORIGINAL,
    extension: (GlideRequest<Drawable>.() -> GlideRequest<Drawable>)? = null,
    withError: Boolean = true
): Drawable? = suspendCoroutine { continuation ->

    val placeholder = CoverUtils.getGradient(this, mediaId)

    val error = GlideApp.with(this)
        .load(placeholder)
        .extend(extension)

    GlideApp.with(this)
        .load(mediaId)
        .override(size)
        .priority(Priority.IMMEDIATE)
        .extend(extension)
        .onlyRetrieveFromCache(true)
        .into(object : CustomTarget<Drawable>() {

            override fun onLoadCleared(placeholder: Drawable?) {
                continuation.resume(null)
            }

            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                continuation.resume(resource)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                if (withError) {
                    error.into(object : CustomTarget<Drawable>() {
                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                            continuation.resume(resource)
                        }

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            continuation.resume(null)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            continuation.resume(null)
                        }
                    })

                } else {
                    continuation.resume(null)
                }
            }
        })
}

fun Context.getDrawableAsync(
    mediaId: MediaId,
    size: Int = Target.SIZE_ORIGINAL,
    action: (Drawable) -> Unit
) {

    val placeholder = CoverUtils.getGradient(this, mediaId)

    val error = GlideApp.with(this)
        .load(placeholder.toBitmap())
        .override(size)

    GlideApp.with(this)
        .load(mediaId)
        .error(error)
        .override(size)
        .priority(Priority.IMMEDIATE)
        .into(object : CustomTarget<Drawable>() {

            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                action(resource)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                errorDrawable?.let { action(it) }
            }

            override fun onLoadCleared(placeholder: Drawable?) {}
        })
}

internal fun GlideRequest<Drawable>.extend(func: (GlideRequest<Drawable>.() -> GlideRequest<Drawable>)?): GlideRequest<Drawable> {
    if (func != null) {
        return this.func()
    }
    return this
}