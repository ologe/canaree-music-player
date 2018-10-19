package dev.olog.msc.utils.k.extension

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Priority
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import dev.olog.msc.app.GlideApp
import dev.olog.msc.app.GlideRequest
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.assertBackgroundThread
import dev.olog.msc.utils.img.CoverUtils
import dev.olog.msc.utils.img.ImageUtils

fun Context.getBitmapAsync(
        model: DisplayableItem,
        size: Int,
        extension: (GlideRequest<Bitmap>.() -> GlideRequest<Bitmap>)? = null,
        withError: Boolean = true): Bitmap {

    assertBackgroundThread()

    val placeholder = CoverUtils.getGradient(this, model.mediaId)

    val onlyFromCache = !ImageUtils.isRealImage(model.image)
    val load : Any = if (!onlyFromCache) model.image else model

    val error = GlideApp.with(this)
            .asBitmap()
            .load(placeholder.toBitmap())
            .override(size)
            .extend(extension)

    val builder = GlideApp.with(this)
            .asBitmap()
            .load(load)
            .override(size)
            .priority(Priority.IMMEDIATE)
            .onlyRetrieveFromCache(onlyFromCache)
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

fun Context.getBitmap(
        model: DisplayableItem,
        size: Int,
        action: (Bitmap) -> Unit
){

    val placeholder = CoverUtils.getGradient(this, model.mediaId)

    val onlyFromCache = !ImageUtils.isRealImage(model.image)
    val load : Any = if (!onlyFromCache) model.image else model

    val error = GlideApp.with(this)
            .asBitmap()
            .load(placeholder.toBitmap())
            .override(size)


    GlideApp.with(this)
            .asBitmap()
            .load(load)
            .error(error)
            .override(size)
            .priority(Priority.IMMEDIATE)
            .onlyRetrieveFromCache(onlyFromCache)
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