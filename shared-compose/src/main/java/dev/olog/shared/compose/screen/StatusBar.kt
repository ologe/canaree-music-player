package dev.olog.shared.compose.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.olog.shared.compose.ThemePreviews
import dev.olog.shared.compose.theme.CanareeTheme
import dev.olog.shared.compose.theme.Theme

@Composable
fun StatusBar(
    modifier: Modifier = Modifier,
    color: Color = Theme.colors.background,
) {
    if (LocalInspectionMode.current) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(24.dp)
                .background(color)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val iconSize = 10.dp
            val iconColor = Theme.colors.iconColor.enabled
            Spacer(modifier = Modifier.weight(1f))
            Spacer(
                Modifier
                    .size(iconSize)
                    .background(iconColor, RectangleShape))
            Spacer(
                Modifier
                    .size(iconSize)
                    .background(iconColor, CircleShape))
            Spacer(
                Modifier
                    .size(iconSize)
                    .background(iconColor, Triangle))
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(color)
        ) {
            Spacer(Modifier.statusBarsPadding())
        }
    }
}

private val Triangle: Shape = object : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply { fillType = PathFillType.EvenOdd }
        path.lineTo(size.width, 0f)
        path.lineTo(size.width / 2f, size.height)
        path.close()
        return Outline.Generic(path)
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        StatusBar()
    }
}