package dev.olog.shared.compose.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import dev.olog.shared.compose.theme.Theme

@Composable
fun CurrentlyPlaying(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
) {
    val scaleState = animateFloatAsState(if (isPlaying) 1f else 0f, label = "scale")
    Spacer(
        modifier
            .width(2.dp)
            .height(20.dp)
            .graphicsLayer {
                scaleY = scaleState.value
            }
            .background(Theme.colors.accent.enabled)
    )
}