package dev.olog.shared.compose.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toolingGraphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.olog.shared.compose.theme.Theme

@Composable
fun Icon(
    painter: Painter,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    enabled: Boolean = true,
    colorFilter: Color = Theme.colors.iconColor.resolve(enabled),
) {
    Spacer(
        modifier = modifier
            .toolingGraphicsLayer()
            .size(size)
            .paint(
                painter = painter,
                colorFilter = ColorFilter.tint(colorFilter),
                contentScale = ContentScale.Fit,
            )
    )
}