package dev.olog.compose.animation

import androidx.compose.animation.core.Easing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlin.math.pow

@Composable
fun rememberAccelerateEasing(factor: Float = 1f): Easing {
    return remember(factor) { AccelerateEasing(factor) }
}

@Composable
fun rememberDecelerateEasing(factor: Float = 1f): Easing {
    return remember(factor) { DecelerateEasing(factor) }
}

/**
 * Adaptation of [android.view.animation.BounceInterpolator]
 */
val BounceEasing = Easing { input ->
    var t = input
    t *= 1.1226f
    if (t < 0.3535f) bounce(t)
    else if (t < 0.7408f) bounce(t - 0.54719f) + 0.7f;
    else if (t < 0.9644f) bounce(t - 0.8526f) + 0.9f;
    else bounce(t - 1.0435f) + 0.95f;
}

@Suppress("NOTHING_TO_INLINE")
private inline fun bounce(input: Float): Float = input * input * 8f

/**
 * Adaptation of [android.view.animation.AccelerateInterpolator]
 */
@Suppress("FunctionName")
internal fun AccelerateEasing(factor: Float) = Easing { input ->
    if (factor == 1f) {
        input * input
    } else {
        input.pow(factor * 2)
    }
}

/**
 * Adaptation of [android.view.animation.DecelerateInterpolator]
 */
@Suppress("FunctionName")
internal fun DecelerateEasing(factor: Float) = Easing { input ->
    if (factor == 1f) {
        1f - (1f - input) * (1f - input)
    } else {
        1f - (1f - input).pow(2 * factor)
    }
}