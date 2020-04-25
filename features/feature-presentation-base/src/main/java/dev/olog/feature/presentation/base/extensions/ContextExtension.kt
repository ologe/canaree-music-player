@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.feature.presentation.base.extensions

import android.content.Context
import android.util.TypedValue
import android.widget.Toast
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat

inline fun Context.getAnimatedVectorDrawable(@DrawableRes id: Int): AnimatedVectorDrawableCompat {
    return AnimatedVectorDrawableCompat.create(this, id)!!
}

//returns dip(dp) dimension value in pixels
inline fun Context.dip(value: Int): Int = (value * resources.displayMetrics.density).toInt()

inline fun Context.dipf(value: Int): Float = (value * resources.displayMetrics.density)

inline fun Context.dimen(@DimenRes resource: Int): Int = resources.getDimensionPixelSize(resource)
inline fun Context.dimenf(@DimenRes resource: Int): Float = resources.getDimensionPixelSize(resource).toFloat()

inline fun Context.dpToPx(dp: Float): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
        .toInt()
}

// TODO use snackbar when possible
inline fun Context.toast(message: Int): Toast = Toast
    .makeText(this, message, Toast.LENGTH_SHORT)
    .apply {
        show()
    }

inline fun Context.toast(message: CharSequence): Toast = Toast
    .makeText(this, message, Toast.LENGTH_SHORT)
    .apply {
        show()
    }

