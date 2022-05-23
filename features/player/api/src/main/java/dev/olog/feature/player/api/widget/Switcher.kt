package dev.olog.feature.player.api.widget

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.olog.core.MediaId

enum class SwitcherDirection {
    None,
    Previous,
    Next
}

private const val Duration = 250

private val NoneEnterTransition = fadeIn(animationSpec = tween(Duration))
private val NoneExitTransition = fadeOut(animationSpec = tween(Duration))

private val PreviousEnterTransition = slideInHorizontally(
    animationSpec = tween(Duration),
    initialOffsetX = { -it },
)
private val PreviousExitTransition = slideOutHorizontally(
    animationSpec = tween(Duration),
    targetOffsetX = { it }
)

private val NextEnterTransition = slideInHorizontally(
    animationSpec = tween(Duration),
    initialOffsetX = { it }
)
private val NextExitTransition = slideOutHorizontally(
    animationSpec = tween(Duration),
    targetOffsetX = { -it }
)

// todo improve, AnimatedContent handles only 2 views and it breaks on fast clicks
@Composable
fun Switcher(
    mediaId: MediaId,
    direction: SwitcherDirection,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.(MediaId) -> Unit
) {
    AnimatedContent(
        modifier = modifier,
        targetState = mediaId,
        transitionSpec = {
            when (direction) {
                SwitcherDirection.None -> NoneEnterTransition with NoneExitTransition
                SwitcherDirection.Previous -> PreviousEnterTransition with PreviousExitTransition
                SwitcherDirection.Next -> NextEnterTransition with NextExitTransition
            }
        },
        content = content,
    )
}