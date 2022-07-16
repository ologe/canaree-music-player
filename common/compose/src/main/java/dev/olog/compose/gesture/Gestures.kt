package dev.olog.compose.gesture

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput

private val animationSpec = tween<Float>(
    durationMillis = 250
)

/**
 * Scale from 1 to [scaleTo] on touch, returns to 1 when not touched anymore
 */
fun Modifier.scalableContent(scaleTo: Float): Modifier = composed {
    var scaleIn by remember {
        mutableStateOf(false)
    }
    val scale = animateFloatAsState(
        targetValue = if (scaleIn) scaleTo else 1f,
        animationSpec = animationSpec
    )

    touchable { scaleIn = it }
        .graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
        }
}

/**
 * Callback to react on touches (up and down)
 */
fun Modifier.touchable(isTouched: (Boolean) -> Unit): Modifier {
    return pointerInput(Unit) {
        forEachGesture {
            awaitPointerEventScope {
                try {
                    awaitFirstDown()
                    isTouched(true)
                    waitForUpOrCancellation()
                } finally {
                    isTouched(false)
                }
            }
        }
    }
}