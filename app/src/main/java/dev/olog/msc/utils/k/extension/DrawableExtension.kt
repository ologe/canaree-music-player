package dev.olog.msc.utils.k.extension

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.TransitionDrawable
import android.support.annotation.ColorInt
import android.support.v4.graphics.drawable.DrawableCompat
import com.bumptech.glide.load.resource.gif.GifDrawable

fun Drawable.getBitmap(): Bitmap? {
    if (this is TransitionDrawable) {
        layers.forEach {
            val bmp = it.getBitmap()
            if (bmp != null) return bmp
        }
    }
    if (this is BitmapDrawable) {
        return bitmap
    } else if (this is GifDrawable) {
        return firstFrame
    }
    return null
}

val LayerDrawable.layers: List<Drawable>
    get() = (0 until numberOfLayers).map { getDrawable(it) }

fun Drawable.tint(@ColorInt color: Int){
    DrawableCompat.setTint(this, color)
}