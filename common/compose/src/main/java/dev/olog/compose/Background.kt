package dev.olog.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier

@Composable
fun Background(
    modifier: Modifier = Modifier,
    withStatusBar: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier.background(MaterialTheme.colors.background),
        content = {
            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colors.onBackground,
                LocalContentAlpha provides 1f,
            ) {
                if (withStatusBar) {
                    StatusBar()
                }
                Box(content = content)
            }
        },
    )
}