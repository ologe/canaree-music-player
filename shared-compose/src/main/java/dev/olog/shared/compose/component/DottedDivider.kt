package dev.olog.shared.compose.component

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DottedDivider(modifier: Modifier = Modifier) {
    if (isSystemInDarkTheme()) {
        return
    }
    val dashWidth = 2.dp
    val dashGap = 2.dp
    val color = Color.Black.copy(alpha = .1f)

    Spacer(
        modifier
            .fillMaxWidth()
            .height(1.dp)
            .drawBehind {
                var x = 0f
                while (x < size.width) {
                    drawRect(
                        color = color,
                        topLeft = Offset(x, 0f),
                        size = Size(dashWidth.toPx(), size.height)
                    )
                    x += (dashWidth + dashGap).toPx()
                }
            }
    )
}