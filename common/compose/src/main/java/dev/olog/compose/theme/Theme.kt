package dev.olog.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider


@Composable
fun CanareeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colors = colors(darkTheme),
        typography = Typography,
        content = {
            CompositionLocalProvider(
                LocalContentAlpha provides 1f,
                content = content,
            )
        }
    )
}