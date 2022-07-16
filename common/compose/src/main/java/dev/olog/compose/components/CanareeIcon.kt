package dev.olog.compose.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.olog.compose.ThemePreviews
import dev.olog.compose.theme.CanareeTheme
import dev.olog.ui.R

private val IconButtonSize = 48.dp
private val IconSize = 24.dp
private val LightThemeTint = Color(0xff_606367)
private val DarkThemeTint = Color(0xff_F5F5F5)

@Composable
fun CanareeIcon(
    painter: Painter,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    tint: Color = if (isSystemInDarkTheme()) DarkThemeTint else LightThemeTint,
) {
    Icon(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier
            .size(IconButtonSize)
            .padding((IconButtonSize - IconSize) / 2),
        tint = tint,
    )
}

@Composable
fun CanareeIcon(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    tint: Color = if (isSystemInDarkTheme()) DarkThemeTint else LightThemeTint,
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier
            .size(IconButtonSize)
            .padding((IconButtonSize - IconSize) / 2),
        tint = tint,
    )
}

@Composable
fun CanareeIconButton(
    painter: Painter,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    tint: Color = if (isSystemInDarkTheme()) DarkThemeTint else LightThemeTint,
    onClick: () -> Unit,
) {
    IconButton(
        modifier = modifier.size(IconButtonSize),
        onClick = onClick,
    ) {
        Icon(
            painter = painter,
            contentDescription = contentDescription,
            modifier = Modifier.size(IconSize),
            tint = tint,
        )
    }
}

@Composable
fun CanareeIconButton(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    tint: Color = if (isSystemInDarkTheme()) DarkThemeTint else LightThemeTint,
    onClick: () -> Unit,
) {
    IconButton(
        modifier = modifier.size(IconButtonSize),
        onClick = onClick,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = Modifier.size(IconSize),
            tint = tint,
        )
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        CanareeBackground {
            Column {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    CanareeIcon(imageVector = Icons.Default.Shuffle)
                    CanareeIcon(painter = painterResource(id = R.drawable.vd_bird_singing))
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    CanareeIconButton(imageVector = Icons.Default.Shuffle) { }
                    CanareeIconButton(painter = painterResource(id = R.drawable.vd_bird_singing)) { }
                }
            }
        }
    }
}