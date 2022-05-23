package dev.olog.compose.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.olog.compose.Background
import dev.olog.compose.ThemePreviews

private val DefaultPrimaryColor = Color(0xff_3D5AFE)

@Composable
internal fun colors(isDarkMode: Boolean): Colors {
    return if (isDarkMode) {
        darkColors(
            background = Color(0xff_121212),
            onBackground = Color.White,
            surface = Color(0xff_222326),
            onSurface = Color.White,
            primary = Color(0xff_121212),
            primaryVariant = Color(0xff_121212),
            onPrimary = Color.White,
            secondary = DefaultPrimaryColor,
            secondaryVariant = DefaultPrimaryColor,
        )
    } else {
        lightColors(
            background = Color.White,
            onBackground = Color(0xff_2b2b2b),
            surface = Color.White,
            onSurface = Color(0xff_2b2b2b),
            primary = Color.White,
            primaryVariant = Color.White,
            onPrimary = Color(0xff_2b2b2b),
            secondary = DefaultPrimaryColor,
            secondaryVariant = DefaultPrimaryColor,
        )
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        Background {
            LazyVerticalGrid(columns = GridCells.Fixed(3)) {
                item {
                    ColorPreview(
                        name = "background",
                        color = MaterialTheme.colors.background,
                        onColor = MaterialTheme.colors.onBackground,
                    )
                }
                item {
                    ColorPreview(
                        name = "surface",
                        color = MaterialTheme.colors.surface,
                        onColor = MaterialTheme.colors.onSurface,
                    )
                }
                item {
                    ColorPreview(
                        name = "primary",
                        color = MaterialTheme.colors.primary,
                        onColor = MaterialTheme.colors.onPrimary,
                    )
                }
                item {
                    ColorPreview(
                        name = "primary var",
                        color = MaterialTheme.colors.primaryVariant,
                        onColor = MaterialTheme.colors.onPrimary,
                    )
                }
                item {
                    ColorPreview(
                        name = "secondary",
                        color = MaterialTheme.colors.secondary,
                        onColor = MaterialTheme.colors.onSecondary,
                    )
                }
                item {
                    ColorPreview(
                        name = "secondary var",
                        color = MaterialTheme.colors.secondaryVariant,
                        onColor = MaterialTheme.colors.onSecondary,
                    )
                }
                item {
                    ColorPreview(
                        name = "error",
                        color = MaterialTheme.colors.error,
                        onColor = MaterialTheme.colors.onError,
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorPreview(
    name: String,
    color: Color,
    onColor: Color? = null,
) {
    Column(Modifier.padding(4.dp)) {
        Text(text = name)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            if (onColor != null) {
                Text(
                    text = "Text",
                    color = onColor
                )
            }
        }
    }
}