@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.shared.android.extensions

import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat

inline fun Drawable.tint(@ColorInt color: Int){
    DrawableCompat.setTint(this, color)
}