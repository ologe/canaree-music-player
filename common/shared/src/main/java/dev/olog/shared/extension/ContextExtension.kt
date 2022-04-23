package dev.olog.shared.extension

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import dev.olog.shared.isOreo

fun Context.getAnimatedVectorDrawable (@DrawableRes id: Int): AnimatedVectorDrawableCompat {
    return AnimatedVectorDrawableCompat.create(this, id)!!
}

fun Context.dip(value: Int): Int = (value * resources.displayMetrics.density).toInt()
fun Context.dip(value: Float): Int = (value * resources.displayMetrics.density).toInt()
fun Context.dipf(value: Int): Float = (value * resources.displayMetrics.density)

fun Context.dimen(@DimenRes resId: Int): Int = resources.getDimensionPixelSize(resId)

fun Context.toast(@StringRes resId: Int): Toast = Toast
        .makeText(this, resId, Toast.LENGTH_SHORT)
        .apply {
            show()
        }

fun Context.toast(message: CharSequence): Toast = Toast
        .makeText(this, message, Toast.LENGTH_SHORT)
        .apply {
            show()
        }

inline val Context.configuration: Configuration
    get() = resources.configuration

inline val Context.isTablet: Boolean
    get() = configuration.smallestScreenWidthDp >= 600

fun Context.vibrate(time: Long){
    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if(isOreo()){
        val effect = VibrationEffect.createOneShot(time, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(effect)
    } else {
        vibrator.vibrate(time)
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun Context.isDarkMode(): Boolean {
    return configuration.isDarkMode()
}

inline fun <reified T : Any> Context.findInContext(): T {
    var context: Context = this
    while (context is ContextWrapper) {
        if (context is T) {
            return context
        }
        context = context.baseContext
    }
    error("$this does not implement ${T::class.java}")
}