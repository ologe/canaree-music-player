package dev.olog.msc.utils.k.extension

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Priority
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import dev.olog.core.MediaId
import dev.olog.image.provider.CoverUtils
import dev.olog.image.provider.GlideApp
import dev.olog.image.provider.GlideRequest
import dev.olog.shared.utils.assertBackgroundThread

//TODO remove after migrating to coroutines
fun Context.getCachedBitmap(
    mediaId: MediaId,
    size: Int,
    extension: (GlideRequest<Bitmap>.() -> GlideRequest<Bitmap>)? = null,
    withError: Boolean = true): Bitmap {

    assertBackgroundThread()

    val placeholder = CoverUtils.getGradient(this, mediaId)

    val error = GlideApp.with(this)
            .asBitmap()
            .load(placeholder.toBitmap())
            .override(size)
            .extend(extension)

    val builder = GlideApp.with(this)
            .asBitmap()
            .load(mediaId)
            .override(size)
            .priority(Priority.IMMEDIATE)
            .onlyRetrieveFromCache(true)
            .extend(extension)

    return try {
        builder.submit().get()
    } catch (ex: Exception){
        if (withError){
            error.submit().get()
        } else {
            throw NullPointerException()
        }
    }

}

@Suppress("DEPRECATION")
fun Context.getBitmapAsync(
    mediaId: MediaId,
    size: Int,
    action: (Bitmap) -> Unit
){

    val placeholder = CoverUtils.getGradient(this, mediaId)
    val error = GlideApp.with(this)
            .asBitmap()
            .load(placeholder.toBitmap())
            .override(size)

    GlideApp.with(this)
            .asBitmap()
            .load(mediaId)
            .error(error)
            .override(size)
            .priority(Priority.IMMEDIATE)
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    action(resource)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    errorDrawable?.let { action(it.toBitmap()) }
                }
            })
}

private fun GlideRequest<Bitmap>.extend(func: (GlideRequest<Bitmap>.() -> GlideRequest<Bitmap>)?): GlideRequest<Bitmap> {
    if (func != null){
        return this.func()
    }
    return this
}