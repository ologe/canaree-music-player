package dev.olog.shared.components.sliding.panel

import androidx.annotation.FloatRange
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.WithConstraints
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SlidingPanel(
    modifier: Modifier = Modifier,
    peek: Dp = 100.dp,
    slidingPanelState: SlidingPanelState = rememberSlidingPanelState(),
    @FloatRange(from = 0.0, to = 1.0) scrimAlpha: Float = 0.4f,
    scrimColor: Color = MaterialTheme.colors.onSurface,
    content: @Composable StackScope.() -> Unit
) {
    Stack {
        Scrim(slidingPanelState, scrimColor, scrimAlpha)
        Panel(modifier, peek, slidingPanelState, content)
    }
}

@Composable
private fun Scrim(
    slidingPanelState: SlidingPanelState,
    scrimColor: Color,
    scrimAlpha: Float
) {
    Canvas(Modifier.fillMaxSize()) {
        drawRect(
            color = scrimColor,
            alpha = (slidingPanelState.fraction * scrimAlpha).coerceIn(0f, scrimAlpha)
        )
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun Panel(
    modifier: Modifier,
    peek: Dp,
    slidingPanelState: SlidingPanelState,
    content: @Composable StackScope.() -> Unit
) {
    WithConstraints(
        modifier.defaultMinSizeConstraints(minHeight = peek)
    ) {
        val max = constraints.maxHeight.toFloat()
        val min = constraints.minHeight.toFloat()

        // translation y from screen top
        val anchors = mapOf(
            0f to SlidingPanelValue.Expanded,
            max - min to SlidingPanelValue.Collapsed
        )
        slidingPanelState.delta = max - min

        Stack(
            Modifier
                .fillMaxSize()
                .offsetPx(y = slidingPanelState.offset)
                .background(Color.Gray)
                .clickable(
                    onClick = {
                        if (slidingPanelState.isCollapsed) {
                            slidingPanelState.expand()
                        }
                    },
                    indication = null
                )
                .swipeable(
                    state = slidingPanelState,
                    anchors = anchors,
                    thresholds = { _, _ ->
                        FixedThreshold(56.dp) // TODO test different values
                    },
                    orientation = Orientation.Vertical,
                    enabled = true,
                    reverseDirection = false,
                    resistance = SwipeableConstants.defaultResistanceConfig(anchors.keys, factorAtMin = 0f)
                ),
            children = content
        )
    }
}