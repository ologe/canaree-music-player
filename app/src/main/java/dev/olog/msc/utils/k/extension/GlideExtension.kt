package dev.olog.msc.utils.k.extension

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.graphics.drawable.toBitmap
import com.bumptech.glide.Priority
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import dev.olog.msc.app.GlideApp
import dev.olog.msc.app.GlideRequest
import dev.olog.msc.presentation.model.DisplayableItem
import dev.olog.msc.utils.img.CoverUtils
import dev.olog.msc.utils.img.ImageUtils
import dev.olog.msc.utils.isMainThread

fun Context.getBitmap(
        model: DisplayableItem,
        size: Int,
        action: (Bitmap) -> Unit,
        extend: (GlideRequest<Bitmap>.() -> GlideRequest<Bitmap>)? = null){

    val placeholder = CoverUtils.getGradient(this, model.mediaId)

    val onlyFromCache = !ImageUtils.isRealImage(this, model.image)
    val load : Any = if (!onlyFromCache)
        model.image else model

    var error = GlideApp.with(this)
            .asBitmap()
            .load(placeholder.toBitmap())
            .onlyRetrieveFromCache(onlyFromCache)
            .priority(Priority.IMMEDIATE)
            .override(size)

    extend?.let { error = error.it() }

    var builder = GlideApp.with(this)
            .asBitmap()
            .load(load)
            .override(size)
            .priority(Priority.IMMEDIATE)
            .onlyRetrieveFromCache(onlyFromCache)
            .error(error)

    extend?.let { builder = builder.it() }

    if (isMainThread()){
        builder.into(object : SimpleTarget<Bitmap>(){
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                action(resource)
            }

            override fun onLoadFailed(errorDrawable: Drawable?) {
                action(placeholder.toBitmap())
            }
        })
    } else {
        val bitmap = try {
            builder.submit(size, size).get()
        } catch (ex: Exception){
            GlideApp.with(this)
                    .asBitmap()
                    .load(placeholder.toBitmap())
                    .priority(Priority.IMMEDIATE)
                    .submit(size, size)
                    .get()
        }
        action(bitmap)
    }

}