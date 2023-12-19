package dev.olog.shared.compose.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.olog.shared.compose.ThemePreviews
import dev.olog.shared.compose.theme.CanareeTheme
import dev.olog.shared.compose.theme.Theme

@Composable
fun EmptyState(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = Theme.typography.header,
        )
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        Box(Modifier.background(Theme.colors.background)) {
            EmptyState(text = "Empty state")
        }
    }
}