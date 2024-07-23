package dev.olog.presentation.player.ui

import android.animation.ObjectAnimator
import android.view.View
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import dev.olog.presentation.R
import dev.olog.shared.android.extensions.dipf

private val FastOutSlowInInterpolator = FastOutSlowInInterpolator()
private const val Duration = 300L

fun View.animateNowPlaying(isPlaying: Boolean) {
    val targetAlpha = if (isPlaying) 1f else .4f
    val previousTargetValue = getTargetValue<Float>()
    setTargetValue(targetAlpha)
    if (previousTargetValue == null || previousTargetValue == targetAlpha) {
        // snap
        alpha = targetAlpha
        return
    }
    // animate
    ObjectAnimator.ofFloat(this, "alpha", alpha, targetAlpha)
        .setDuration(Duration)
        .apply { interpolator = FastOutSlowInInterpolator }
        .start()
}

fun View.animateElevation(isPlaying: Boolean) {
    val targetElevation = if (isPlaying) context.dipf(10) else 0f
    val previousTargetValue = getTargetValue<Float>()
    setTargetValue(targetElevation)
    if (previousTargetValue == null || previousTargetValue == targetElevation) {
        // snap
        translationZ = targetElevation
        return
    }
    ObjectAnimator.ofFloat(this, "translationZ", translationZ, targetElevation)
        .setDuration(Duration)
        .apply { interpolator = FastOutSlowInInterpolator }
        .start()
}

@Suppress("UNCHECKED_CAST")
private fun <T> View.getTargetValue(): T? {
    return getTag(R.id.animation_target_value) as? T
}

private fun <T> View.setTargetValue(value: T) {
    setTag(R.id.animation_target_value, value)
}