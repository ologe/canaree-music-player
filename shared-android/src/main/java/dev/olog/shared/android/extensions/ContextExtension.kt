@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.shared.android.extensions

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.widget.Toast
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import dev.olog.shared.android.R

inline fun Context.getAnimatedVectorDrawable (@DrawableRes id: Int): AnimatedVectorDrawableCompat {
    return AnimatedVectorDrawableCompat.create(this, id)!!
}

//returns dip(dp) dimension value in pixels
inline fun Context.dip(value: Int): Int = (value * resources.displayMetrics.density).toInt()
inline fun Context.dip(value: Float): Int = (value * resources.displayMetrics.density).toInt()
inline fun Context.dipf(value: Int): Float = (value * resources.displayMetrics.density)

inline fun Context.dimen(@DimenRes resource: Int): Int = resources.getDimensionPixelSize(resource)

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

inline val Context.configuration: android.content.res.Configuration
    get() = resources.configuration

inline val Context.isTablet: Boolean
    get() = configuration.smallestScreenWidthDp >= 600

inline fun Context.scrimBackground(): Int {
    return themeAttributeToColor(com.google.android.material.R.attr.scrimBackground)
}

inline fun Context.textColorPrimary(): Int {
    return themeAttributeToColor(android.R.attr.textColorPrimary)
}

inline fun Context.textColorSecondary(): Int {
    return themeAttributeToColor(android.R.attr.textColorSecondary)
}

inline fun Context.colorSurface(): Int {
    return themeAttributeToColor(com.google.android.material.R.attr.colorSurface)
}

inline fun Context.colorBackground():Int {
    return themeAttributeToColor(android.R.attr.colorBackground)
}

inline fun Context.colorPrimary(): Int {
    return themeAttributeToColor(com.google.android.material.R.attr.colorPrimary)
}

inline fun Context.colorAccent(): Int {
    return themeAttributeToColor(com.google.android.material.R.attr.colorAccent)
}

inline fun Context.colorControlNormal(): Int {
    return themeAttributeToColor(com.google.android.material.R.attr.colorControlNormal)
}

inline fun Context.colorPrimaryId(): Int {
    return themeAttributeToResId(com.google.android.material.R.attr.colorPrimary)
}

inline fun Context.isDarkMode(): Boolean {
    return resources.getBoolean(R.bool.is_dark_mode)
}

inline fun Context.colorSwipeBackground(): Int {
    return themeAttributeToColor(R.attr.colorSwipeBackground)
}

fun Context.themeAttributeToColor(themeAttributeId: Int, fallbackColor: Int = Color.WHITE): Int {
    val outValue = TypedValue()
    val theme = this.theme
    val resolved = theme.resolveAttribute(themeAttributeId, outValue, true)
    if (resolved) {
        return ContextCompat.getColor(this, outValue.resourceId)
    }
    return fallbackColor
}

fun Context.themeAttributeToResId(themeAttributeId: Int): Int {
    val outValue = TypedValue()
    val theme = this.theme
    val resolved = theme.resolveAttribute(themeAttributeId, outValue, true)
    if (resolved) {
        return outValue.resourceId
    }
    return -1
}

inline fun <reified T : Any> Context.systemService(): T = getSystemService<T>()!!