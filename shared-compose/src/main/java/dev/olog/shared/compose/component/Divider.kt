package dev.olog.shared.compose.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.dp
import dev.olog.shared.compose.ThemePreviews
import dev.olog.shared.compose.theme.CanareeTheme
import dev.olog.shared.compose.theme.Theme

@Composable
fun Divider(
    modifier: Modifier = Modifier,
) {
    if (isSystemInDarkTheme()) {
        Spacer(modifier = modifier)
        return
    }

    val color = Color.Black.copy(alpha = .1f)
    val height = 1.dp
    val width = 2.dp
    val gap = width

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        drawLine(
            color = color,
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(width.toPx(), gap.toPx()), 10f
            ),
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = height.toPx(),
        )
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        Box(Modifier.background(Theme.colors.background)) {
            Divider()
        }
    }
}