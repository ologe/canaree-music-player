@file:Suppress("NOTHING_TO_INLINE")

package dev.olog.platform.extension

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.TypedValue
import android.widget.Toast
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import dev.olog.platform.R

inline fun Context.getAnimatedVectorDrawable (@DrawableRes id: Int): AnimatedVectorDrawableCompat {
    return AnimatedVectorDrawableCompat.create(this, id)!!
}

//returns dip(dp) dimension value in pixels
inline fun Context.dip(value: Int): Int = (value * resources.displayMetrics.density).toInt()
inline fun Context.dip(value: Float): Int = (value * resources.displayMetrics.density).toInt()
inline fun Context.dipf(value: Int): Float = (value * resources.displayMetrics.density)

inline fun Context.dimen(@DimenRes resource: Int): Int = resources.getDimensionPixelSize(resource)

inline fun Context.px2dip(px: Int): Float = px.toFloat() / resources.displayMetrics.density
inline fun Context.px2sp(px: Int): Float = px.toFloat() / resources.displayMetrics.scaledDensity

inline fun Context.dpToPx(dp: Float): Int {
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

inline val Context.isTablet: Boolean
    get() = configuration.smallestScreenWidthDp >= 600

@SuppressLint("NewApi")
@Suppress("DEPRECATION")
fun Context.vibrate(time: Long){
    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        val effect = VibrationEffect.createOneShot(time, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(effect)
    } else {
        vibrator.vibrate(time)
    }
}

inline fun Context.isDarkMode(): Boolean {
    val configuration = resources.configuration
    return (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
}

inline fun <reified T : Any> Context.findInContext(): T {
    var context: Context = this
    while (context is ContextWrapper) {
        if (context is T) {
            return context
        }
        context = context.baseContext
    }
    if (context is T) {
        return context
    }
    error("$this does not implement ${T::class.java}")
}