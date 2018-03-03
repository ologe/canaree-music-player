package dev.olog.msc.utils.k.extension

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.webkit.URLUtil
import androidx.graphics.drawable.toBitmap
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
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
        val prova = Uri.parse(image)
        println(prova.path)
        println(File(prova.path).exists())
    }catch (ex: Exception){

    }

    val uri = Uri.fromFile(File(image))
    val realImage = when {
        File(uri.path).exists() -> uri
        URLUtil.isNetworkUrl(image) -> image
        else -> Uri.parse(image)
    }

    var error = GlideApp.with(this)
            .asBitmap()
            .load(placeholder.toBitmap())
            .priority(Priority.IMMEDIATE)
            .override(size)

    extend?.let { error = error.it() }

    var builder = GlideApp.with(this)
            .asBitmap()
            .load(realImage)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .override(size)
            .priority(Priority.IMMEDIATE)
            .error(error)

    extend?.let { builder = builder.it() }


    if (isMainThread()){
        builder.into(object : SimpleTarget<Bitmap>(){
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                action(resource)
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