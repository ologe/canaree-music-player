package dev.olog.shared.compose.component

import android.view.MotionEvent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.shared.android.theme.ImageShape
import dev.olog.shared.compose.theme.LocalThemeSettings
import kotlinx.coroutines.coroutineScope

fun Modifier.dynamicShape(
    mediaId: MediaId,
    radius: Dp = 5.dp,
): Modifier = composed {
    if (mediaId.category == MediaIdCategory.ARTISTS ||
        mediaId.category == MediaIdCategory.PODCASTS_ARTISTS) {
        return@composed Modifier.clip(CircleShape)
    }

    val shape = LocalThemeSettings.current.imageShape

    Modifier.clip(
        when (shape) {
            ImageShape.RECTANGLE -> RectangleShape
            ImageShape.ROUND -> RoundedCornerShape(radius)
            ImageShape.CUT_CORNER -> CutCornerShape(radius)
        }
    )
}

fun Modifier.scaleDownOnTouch(scale: Float = .97f): Modifier = composed {
    var isTouched by remember { mutableStateOf(false) }
    val scaleState = animateFloatAsState(
        targetValue = if (isTouched) scale else 1f,
        label = "scale",
        animationSpec = tween()
    )
    Modifier
        .graphicsLayer {
            scaleX = scaleState.value
            scaleY = scaleState.value
        }
        .pointerInput(Unit) {
            coroutineScope {
                awaitEachGesture {
                    try {
                        awaitFirstDown(requireUnconsumed = true)
                        isTouched = true
                        waitForUpOrCancellation()
                    } finally {
                        isTouched = false
                    }
                }
            }
        }
}

fun Modifier.onActionDown(action: () -> Unit): Modifier {
    return this.pointerInteropFilter { event ->
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                action()
                true
            }
            else -> false
        }
    }
}