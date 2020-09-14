package dev.olog.shared.components

import androidx.annotation.FloatRange
import androidx.compose.foundation.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProgressIndicatorConstants
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ProgressBar(
    @FloatRange(from = 0.0, to = 1.0) progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.secondary,
    backgroundColor: Color = color.copy(alpha = ProgressIndicatorConstants.DefaultIndicatorBackgroundOpacity),
    height: Dp = 2.dp
) {
    Box(Modifier.height(height).clipToBounds()) {
        LinearProgressIndicator(
            progress = progress,
            modifier = modifier,
            color = color,
            backgroundColor = backgroundColor
        )
    }
}