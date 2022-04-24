package dev.olog.ui.extension

import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat

val LayerDrawable.layers: List<Drawable>
    get() = (0 until numberOfLayers).map { getDrawable(it) }

@Suppress("NOTHING_TO_INLINE")
inline fun Drawable.tint(@ColorInt color: Int){
    DrawableCompat.setTint(this, color)
}