package dev.olog.shared.android.extensions

import android.graphics.drawable.Drawable
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat

fun AnimatedVectorDrawableCompat.onEnd(block: () -> Unit) {
    registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
        override fun onAnimationEnd(drawable: Drawable?) {
            block()
        }
    })
}