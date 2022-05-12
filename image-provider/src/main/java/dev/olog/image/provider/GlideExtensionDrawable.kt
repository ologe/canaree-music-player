package dev.olog.image.provider

import android.content.Context
import android.graphics.drawable.Drawable
import com.bumptech.glide.Priority
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import dev.olog.core.MediaId
import dev.olog.shared.extension.safeResume
import dev.olog.ui.CoverUtils
import dev.olog.ui.GlideUtils
import kotlin.coroutines.suspendCoroutine

suspend fun Context.getCachedDrawable(
    mediaId: MediaId,
    size: Int = GlideUtils.OVERRIDE_BIG,
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
                continuation.safeResume(null)
            }

            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                continuation.safeResume(resource)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                if (withError) {
                    error.into(object : CustomTarget<Drawable>() {
                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                            continuation.safeResume(resource)
                        }

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            continuation.safeResume(null)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            continuation.safeResume(null)
                        }
                    })

                } else {
                    continuation.safeResume(null)
                }
            }
        })
}

internal fun GlideRequest<Drawable>.extend(func: (GlideRequest<Drawable>.() -> GlideRequest<Drawable>)?): GlideRequest<Drawable> {
    if (func != null) {
        return this.func()
    }
    return this
}