package dev.olog.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import dev.olog.compose.shape.ProvideImageShapePrefs
import dev.olog.compose.shape.ProvideQuickActionPrefs


@Composable
fun CanareeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colors = colors(darkTheme),
        typography = Typography,
        content = {
            LocalProviders {
                content()
            }
        }
    )
}

@Composable
private fun LocalProviders(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalContentAlpha provides 1f,
        LocalContentColor provides MaterialTheme.colors.onBackground
    ) {
        ProvideImageShapePrefs {
            ProvideQuickActionPrefs {
                content()
            }
        }
    }
}