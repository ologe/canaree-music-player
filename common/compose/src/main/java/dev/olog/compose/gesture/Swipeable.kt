/**
 * credits to https://gist.github.com/darvld/eb3844474baf2f3fc6d3ab44a4b4b5f8
 */
package dev.olog.compose.gesture

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissDirection.EndToStart
import androidx.compose.material.DismissDirection.StartToEnd
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue.Default
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.ThresholdConfig
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.olog.compose.Background
import dev.olog.compose.CanareeIcons
import dev.olog.compose.DevicePreviews
import dev.olog.compose.LaunchedBooleanEffect
import dev.olog.compose.animation.BounceEasing
import dev.olog.compose.animation.rememberAccelerateEasing
import dev.olog.compose.animation.rememberDecelerateEasing
import dev.olog.compose.theme.CanareeTheme
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sqrt

private val DefaultColor = Color(0xff_c6c6c6)
private val PlayNextColor = Color(0xff_364854)
private val DeleteColor = Color(0xff_cf1721)
private const val CircularRevealDuration = 400
private const val TargetIconScale = 1.2f
private val IconSize = 48.dp
private val IconPaddingFromScreen = 16.dp

@Composable
fun CircularSwipeToDismiss(
    state: DismissState = rememberDismissState(),
    directions: Set<DismissDirection> = setOf(EndToStart, StartToEnd),
    dismissThresholds: (DismissDirection) -> ThresholdConfig = { FractionalThreshold(0.5f) },
    onDelete: () -> Boolean = { false }, // true to reset
    onPlayNext: () -> Boolean = { true }, // true to reset
    contentColor: Color = MaterialTheme.colors.background,
    content: @Composable BoxScope.() -> Unit,
) {
    // call callbacks on dismiss finished
    LaunchedEffect(state.isDismissed(EndToStart), state.isDismissed(StartToEnd)) {
        val dismissStartToEnd = state.isDismissed(StartToEnd)
        val dismissEndToStart = state.isDismissed(EndToStart)
        if (dismissEndToStart) {
            if (onPlayNext()) {
                state.reset()
            }
        } else if (dismissStartToEnd) {
            if (onDelete()) {
                state.reset()
            }
        }
    }

    SwipeToDismiss(
        state = state,
        directions = directions,
        dismissThresholds = dismissThresholds,
        background = {
            SwipeableBackground(
                state = state,
                modifier = Modifier.fillMaxSize()
            )
        },
        dismissContent = {
            Box(Modifier.background(contentColor)) {
                 content()
            }
        },
    )
}

@Composable
private fun SwipeableBackground(
    state: DismissState,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        val animatable = remember { Animatable(0f) }
        val scaleAnimatable = remember { Animatable(1f) }
        val accelerateEasing = rememberAccelerateEasing()
        val decelerateEasing = rememberDecelerateEasing()
        val bounceEasing = BounceEasing

        val offset = state.progress.fraction
        LaunchedBooleanEffect(offset > .15f) { // start reveal
            // background
            launch {
                animatable.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = CircularRevealDuration,
                        easing = accelerateEasing,
                    )
                )
            }
            // icon scale
            launch {
                // increase
                scaleAnimatable.animateTo(
                    targetValue = TargetIconScale,
                    animationSpec = tween(
                        durationMillis = CircularRevealDuration / 2,
                        easing = decelerateEasing,
                    )
                )
                // decrease
                scaleAnimatable.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = CircularRevealDuration / 2,
                        easing = bounceEasing,
                    )
                )
            }
        }

        // restore initial state
        LaunchedBooleanEffect(
            predicate1 = offset < 0.01,
            predicate2 = with(state.progress) { from == Default && to == from }
        ) {
            // background
            launch {
                animatable.snapTo(0f)
            }
            // icon scale
            launch {
                scaleAnimatable.snapTo(1f)
            }
        }

        // actions background with circular reveal animation, hide completely if idle
        CircularRevealBackground(
            state = state,
            modifier = Modifier.matchParentSize()
        )

        // todo icons are redrawn on each movement, is this correct?
        //   and seems not related to scale

        // delete icon, left icon
        if (state.dismissDirection == StartToEnd) {
            ActionIcon(
                imageVector = CanareeIcons.Delete,
                scale = scaleAnimatable.value,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = IconPaddingFromScreen)
            )
        }

        // addToQueue icon, right icon
        if (state.dismissDirection == EndToStart) {
            ActionIcon(
                imageVector = CanareeIcons.PlaylistAdd,
                scale = scaleAnimatable.value,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = IconPaddingFromScreen)
            )
        }
    }
}

@Composable
private fun CircularRevealBackground(
    state: DismissState,
    modifier: Modifier = Modifier,
) {
    val animatable = remember { Animatable(0f) }
    val easing = rememberAccelerateEasing()

    val offset = state.progress.fraction
    LaunchedEffect(offset > .15f) { // start reveal
        animatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = CircularRevealDuration,
                easing = easing,
            )
        )
    }
    LaunchedEffect(abs(offset) < 0.01) { // restore initial state
        animatable.snapTo(0f)
    }

    val path = remember { Path() }
    val paddingFromScreen = IconPaddingFromScreen + (IconSize / 2)
    val direction = state.dismissDirection
    val backgroundColor = when (direction) {
        StartToEnd -> DeleteColor
        EndToStart -> PlayNextColor
        null -> Color.Unspecified
    }

    // todo fix background color on multiple fast swipes
    Spacer(
        modifier = modifier
            .background(DefaultColor) // default background
            .drawWithCache {
                path.reset()

                val center = when (direction) {
                    StartToEnd -> Offset(paddingFromScreen.toPx(), size.height / 2)
                    EndToStart -> Offset(size.width - paddingFromScreen.toPx(), size.height / 2)
                    null -> Offset.Zero
                }
                val normalizedOrigin = Offset(
                    center.x / size.width,
                    .5f,
                )
                val radius = calculateRadius(normalizedOrigin, size)
                path.addOval(Rect(center, radius * animatable.value))

                onDrawBehind {
                    clipPath(path) {
                        drawRect(backgroundColor)
                    }
                }
            }
    )
}

private fun calculateRadius(normalizedOrigin: Offset, size: Size) = with(normalizedOrigin) {
    val x = (if (x > 0.5f) x else 1 - x) * size.width
    val y = (if (y > 0.5f) y else 1 - y) * size.height
    sqrt(x * x + y * y)
}

@Composable
private fun ActionIcon(
    imageVector: ImageVector,
    scale: Float,
    modifier: Modifier = Modifier,
) {
    Icon(
        imageVector = imageVector,
        contentDescription = null,
        modifier = modifier
            .size(IconSize)
            .padding(12.dp)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
            ),
        tint = Color.White,
    )
}

@DevicePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        Background(
            Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {
            CircularSwipeToDismiss(
                onDelete = { true },
                onPlayNext = { true },
            ) {
                Spacer(
                    Modifier.fillMaxSize()
                )
            }
        }
    }
}