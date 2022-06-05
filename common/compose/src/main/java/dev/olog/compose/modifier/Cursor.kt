package dev.olog.compose.modifier

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

private val DefaultCursorThickness = 2.dp

private val cursorAnimationSpec: AnimationSpec<Float>
    get() = infiniteRepeatable(
        animation = keyframes {
            durationMillis = 1000
            1f at 0
            1f at 499
            0f at 500
            0f at 999
        }
    )

fun Modifier.cursor(
    hasFocus: Boolean,
    brush: Brush,
) = if (hasFocus) {
    composed {
        val cursorAlpha = remember { Animatable(1f) }

        LaunchedEffect(brush) {
            cursorAlpha.animateTo(0f, cursorAnimationSpec)
        }

        drawWithContent {
            drawContent()

            val cursorAlphaValue = cursorAlpha.value.coerceIn(0f, 1f)
            if (cursorAlphaValue != 0f) {
                val cursorWidth = DefaultCursorThickness.toPx()
                val cursorX = (cursorWidth / 2)
                    .coerceAtMost(size.width - cursorWidth / 2)

                drawLine(
                    brush = brush,
                    start = Offset(cursorX, 0f),
                    end = Offset(cursorX, size.height),
                    alpha = cursorAlphaValue,
                    strokeWidth = cursorWidth
                )
            }
        }
    }
} else this