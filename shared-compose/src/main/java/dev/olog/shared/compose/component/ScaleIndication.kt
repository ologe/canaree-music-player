package dev.olog.shared.compose.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.scale

@Composable
fun rememberScaleIndication(): Indication {
    return remember { ScaleIndication() }
}

private class ScaleIndication : Indication {

    class TestInstance(
        private val scale: State<Float>,
    ) : IndicationInstance {

        override fun ContentDrawScope.drawIndication() {
            scale(scale.value, scale.value) {
                with(this@drawIndication) {
                    drawContent()
                }
            }
        }
    }

    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance {
        val isPressed by interactionSource.collectIsPressedAsState()
        val scale = animateFloatAsState(
            targetValue = if (isPressed) .97f else 1f,
            animationSpec = tween(300, easing = FastOutSlowInEasing)
        )
        return remember(interactionSource) {
            TestInstance(scale)
        }
    }
}