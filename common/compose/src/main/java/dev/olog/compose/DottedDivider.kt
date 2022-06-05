package dev.olog.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.dp
import dev.olog.compose.theme.CanareeTheme

private val Color = Color.Black.copy(.1f)
private val Height = 1.dp
private val Width = 2.dp
private val Gap = Width

@Composable
fun DottedDivider(
    modifier: Modifier = Modifier,
) {
    if (isSystemInDarkTheme()) {
        return
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(Height)
    ) {
        drawLine(
            color = dev.olog.compose.Color,
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(Width.toPx(), Gap.toPx()), 10f
            ),
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = Height.toPx(),
        )
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        Background {
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center,
            ) {
                DottedDivider()
            }
        }
    }
}