package dev.olog.shared.compose.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.olog.shared.compose.ThemePreviews
import dev.olog.shared.compose.theme.CanareeTheme
import dev.olog.shared.compose.theme.LocalContentColor
import dev.olog.shared.compose.theme.Theme

@Composable
fun ButtonText(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
    ) {
        Text(text = text)
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        Column(
            Modifier
                .background(Theme.colors.background)
                .padding(8.dp)
        ) {
            ButtonText(text = "Button") {}
            CompositionLocalProvider(LocalContentColor provides Theme.colors.accent) {
                ButtonText(text = "Button") {}
            }
        }
    }
}