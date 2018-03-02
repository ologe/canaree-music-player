package dev.olog.msc.utils.k.extension

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.graphics.drawable.toBitmap
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import dev.olog.msc.app.GlideApp
import dev.olog.msc.app.GlideRequest
import dev.olog.msc.utils.isMainThread
import java.io.File

fun Context.getBitmap(image: String, placeholder: Drawable,
                      size: Int, action: (Bitmap) -> Unit,
                      extend: (GlideRequest<Bitmap>.() -> GlideRequest<Bitmap>)? = null){

    try {
        val builder = imageBuilder(Uri.parse(image), size, extend)
                .error(imageBuilder(Uri.fromFile(File(image)), size, extend)
                        .error(imageBuilder(image, size, extend)
                                .error(imageBuilder(placeholder, size, extend))
                        )
                )

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
            val bitmap = builder.submit(size, size).get()
            action(bitmap)
        }
    } catch (ex: Exception){
        action(placeholder.toBitmap())
    }

}

private fun Context.imageBuilder(
        image: Any,
        size: Int,
        extend: (GlideRequest<Bitmap>.() -> GlideRequest<Bitmap>)? = null): GlideRequest<Bitmap>{

    var builder = GlideApp.with(this)
            .asBitmap()
            .load(image)
            .override(size)

    extend?.let {
        builder = builder.extend()
    }

    return builder
}