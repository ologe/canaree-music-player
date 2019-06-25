package dev.olog.shared.extensions

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.TransitionDrawable
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat

fun Drawable.getBitmap(): Bitmap? {
    if (this is TransitionDrawable) {
        layers.forEach {
            val bmp = it.getBitmap()
            if (bmp != null) return bmp
        }
    }
    if (this is BitmapDrawable) {
        return bitmap
    }
    return null
}

val LayerDrawable.layers: List<Drawable>
    get() = (0 until numberOfLayers).map { getDrawable(it) }

fun Drawable.tint(@ColorInt color: Int){
    DrawableCompat.setTint(this, color)
}