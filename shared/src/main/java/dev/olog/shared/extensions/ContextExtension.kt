@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.shared.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.TypedValue
import android.widget.Toast
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import dev.olog.shared.R
import dev.olog.shared.utils.isOreo

fun Context.getAnimatedVectorDrawable (@DrawableRes id: Int): AnimatedVectorDrawableCompat {
    return AnimatedVectorDrawableCompat.create(this, id)!!
}

//returns dip(dp) dimension value in pixels
fun Context.dip(value: Int): Int = (value * resources.displayMetrics.density).toInt()
fun Context.dip(value: Float): Int = (value * resources.displayMetrics.density).toInt()
fun Context.dipf(value: Int): Float = (value * resources.displayMetrics.density)

fun Context.dimen(@DimenRes resource: Int): Int = resources.getDimensionPixelSize(resource)

fun Context.px2dip(px: Int): Float = px.toFloat() / resources.displayMetrics.density
fun Context.px2sp(px: Int): Float = px.toFloat() / resources.displayMetrics.scaledDensity

fun Context.dpToPx(dp: Float): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()
}

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

@SuppressLint("NewApi")
@Suppress("DEPRECATION")
fun Context.vibrate(time: Long){
    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if(isOreo()){
        val effect = VibrationEffect.createOneShot(time, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(effect)
    } else {
        vibrator.vibrate(time)
    }
}

fun Context.colorScrim(): Int {
    return themeAttributeToColor(R.attr.colorScrim)
}

fun Context.textColorPrimary(): Int {
    return themeAttributeToColor(android.R.attr.textColorPrimary)
}

fun Context.textColorSecondary(): Int {
    return themeAttributeToColor(android.R.attr.textColorSecondary)
}

fun Context.colorSurface(): Int {
    return themeAttributeToColor(com.google.android.material.R.attr.colorSurface)
}

fun Context.colorBackground():Int {
    return themeAttributeToColor(android.R.attr.colorBackground)
}

fun Context.colorPrimary(): Int {
    return themeAttributeToColor(com.google.android.material.R.attr.colorPrimary)
}

fun Context.colorControlNormal(): Int {
    return themeAttributeToColor(com.google.android.material.R.attr.colorControlNormal)
}

fun Context.colorPrimaryId(): Int {
    return themeAttributeToResId(com.google.android.material.R.attr.colorPrimary)
}

fun Context.isDarkMode(): Boolean {
    return resources.getBoolean(R.bool.is_dark_mode)
}

fun Context.colorSwipeBackground(): Int {
    return themeAttributeToColor(R.attr.colorSwipeBackground)
}

private fun Context.themeAttributeToColor(themeAttributeId: Int, fallbackColor: Int = Color.WHITE): Int {
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