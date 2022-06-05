package dev.olog.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import dev.olog.compose.theme.CanareeTheme

@Composable
fun StatusBar(
    modifier: Modifier = Modifier,
    color: Color = if (isSystemInDarkTheme()) MaterialTheme.colors.surface else MaterialTheme.colors.background
) {
    val heightModifier = if (LocalInspectionMode.current) {
        Modifier.height(24.dp)
    } else {
        Modifier.windowInsetsTopHeight(WindowInsets.statusBars)
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(heightModifier)
            .background(color)
    ) {
        if (LocalInspectionMode.current) {
            val size = 10.dp
            val iconColor = if (isSystemInDarkTheme()) Color.White else Color.DarkGray.copy(alpha = 0.7f)
            Row(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Spacer(
                    modifier = Modifier
                        .size(size)
                        .background(iconColor)
                )
                Spacer(
                    modifier = Modifier
                        .size(size)
                        .clip(CircleShape)
                        .background(iconColor)
                )
                Triangle(iconColor, modifier = Modifier.size(size))
            }
        }
    }
}

@Composable
private fun Triangle(
    color: Color,
    modifier: Modifier = Modifier
) {
    val path by remember {
        val path = Path().apply { fillType = PathFillType.EvenOdd }
        mutableStateOf(path)
    }
    Canvas(modifier = modifier) {
        path.reset()
        path.lineTo(this.size.width, 0f)
        path.lineTo(this.size.width / 2f, this.size.height)
        path.close()
        drawPath(path, color)
    }
}

@ThemePreviews
@Composable
private fun StatusBarPreview() {
    CanareeTheme {
        StatusBar()
    }
}