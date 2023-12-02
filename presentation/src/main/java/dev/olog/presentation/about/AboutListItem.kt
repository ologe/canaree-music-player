package dev.olog.presentation.about

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import dev.olog.shared.compose.ThemePreviews
import dev.olog.shared.compose.component.Text
import dev.olog.shared.compose.theme.CanareeTheme
import dev.olog.shared.compose.theme.Theme

@Composable
fun AboutListItem(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)?,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            )
            .padding(
                vertical = Theme.spacing.small,
                horizontal = Theme.spacing.small,
            ),
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            color = Theme.colors.textColorPrimary.enabled,
        )
        Text(
            text = subtitle,
            color = Theme.colors.textColorSecondary.enabled,
        )
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        Box(Modifier.background(Theme.colors.background)) {
            AboutListItem(
                title = "title",
                subtitle = "subtitle",
                onClick = {}
            )
        }
    }
}