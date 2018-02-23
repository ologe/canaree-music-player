package dev.olog.msc.utils.k.extension

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.drawable.AnimatedVectorDrawable
import android.support.annotation.DimenRes
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.widget.Toast

val Context.isPortrait: Boolean
    get() = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

val Context.isLandscape: Boolean
    get() = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

fun Context.getAnimatedVectorDrawable (@DrawableRes id: Int): AnimatedVectorDrawable {
    return this.getDrawable(id) as AnimatedVectorDrawable
}

fun Context.hasPermission(permission: String) : Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

//returns dip(dp) dimension value in pixels
fun Context.dip(value: Int): Int = (value * resources.displayMetrics.density).toInt()
fun Context.dip(value: Float): Int = (value * resources.displayMetrics.density).toInt()

//return sp dimension value in pixels
fun Context.sp(value: Int): Int = (value * resources.displayMetrics.scaledDensity).toInt()
fun Context.sp(value: Float): Int = (value * resources.displayMetrics.scaledDensity).toInt()

fun Context.dimen(@DimenRes resource: Int): Int = resources.getDimensionPixelSize(resource)

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