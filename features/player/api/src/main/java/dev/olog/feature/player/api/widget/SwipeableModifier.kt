package dev.olog.feature.player.api.widget

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.abs

private val DefaultSkipArea = 64.dp
private const val swipedThreshold = 50

fun Modifier.swipeableModifier(
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onClick: () -> Unit,
    skipAreaWidth: Dp = DefaultSkipArea,
    showDebugArea: Boolean = false,
    disallowParentInterceptEvent: (Boolean) -> Unit = { },
): Modifier = composed {

    val configuration = LocalViewConfiguration.current
    var viewSize by remember { mutableStateOf(IntSize(0, 0)) }

    onSizeChanged {
        viewSize = it
    } then drawWithContent {
        drawContent()
        if (showDebugArea) {
            val color = Color.Yellow.copy(alpha = .2f)
            drawRect(
                color = color,
                topLeft = Offset.Zero,
                size = Size(skipAreaWidth.toPx(), size.height)
            )
            drawRect(
                color = color,
                topLeft = Offset(this.size.width - skipAreaWidth.toPx(), 0f),
                size = Size(skipAreaWidth.toPx(), size.height)
            )
        }
    } then pointerInput(Unit) {
        forEachGesture {
            awaitPointerEventScope {
                handleClick(
                    viewWidth = viewSize.width,
                    viewConfiguration = configuration,
                    disallowParentInterceptEvent = disallowParentInterceptEvent,
                    skipAreaWidth = skipAreaWidth,
                    onSwipeLeft = onSwipeLeft,
                    onSwipeRight = onSwipeRight,
                    onLeftEdgeClick = onSwipeRight,
                    onRightEdgeClick = onSwipeLeft,
                    onClick = onClick
                )
            }
        }
    }
}

private suspend fun AwaitPointerEventScope.handleClick(
    viewWidth: Int,
    viewConfiguration: ViewConfiguration,
    disallowParentInterceptEvent: (Boolean) -> Unit,
    skipAreaWidth: Dp,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onLeftEdgeClick: () -> Unit,
    onRightEdgeClick: () -> Unit,
    onClick: () -> Unit,
) {
    val downEvent = awaitFirstDown(requireUnconsumed = true)

    val (downX, downY) = downEvent.position
    disallowParentInterceptEvent(true)

    while (true) {
        val event = awaitPointerEvent().changes.first()
        val (thisX, thisY) = event.position
        val diffX = abs(thisX - downX)
        val diffY = abs(thisY - downY)
        val swipedHorizontally = diffX > swipedThreshold
        val swipedVertically = diffY > swipedThreshold

        val isHorizontalSwipe = swipedHorizontally && diffX > diffY


        if (event.changedToUp()) {
            if (isHorizontalSwipe) {
                if (thisX > downX) {
                    // right
                    onSwipeRight()
                    event.consume()
                } else if (thisX < downX) {
                    event.consume()
                    onSwipeLeft()
                }
            }
            if (!swipedHorizontally && !swipedVertically) {
                when {
                    downX.toInt() in 0..skipAreaWidth.roundToPx() -> {
                        event.consume()
                        onLeftEdgeClick()
                    }
                    downX.toInt() in viewWidth - skipAreaWidth.roundToPx()..viewWidth -> {
                        event.consume()
                        onRightEdgeClick()
                    }
                    diffX < viewConfiguration.touchSlop && diffY < viewConfiguration.touchSlop -> {
                        event.consume()
                        onClick()
                    }
                }
            }

            disallowParentInterceptEvent(false)

            break

        } else {
            val canScroll = swipedHorizontally || isHorizontalSwipe
            disallowParentInterceptEvent(canScroll)
            if (diffY > viewConfiguration.touchSlop * 2) {
                break
            }
        }
    }
}