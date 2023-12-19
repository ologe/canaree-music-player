package dev.olog.shared.compose.listitem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.olog.shared.compose.ThemePreviews
import dev.olog.shared.compose.component.Divider
import dev.olog.shared.compose.component.Text
import dev.olog.shared.compose.theme.CanareeTheme
import dev.olog.shared.compose.theme.LocalContentColor
import dev.olog.shared.compose.theme.LocalIconColor
import dev.olog.shared.compose.theme.Theme
import dev.olog.shared.compose.theme.toFakeSp

@Composable
fun ListItemHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.padding(vertical = Theme.spacing.small),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        maxLines = 1,
                        fontSize = 14.dp.toFakeSp(),
                        color = Theme.colors.primary.enabled,
                    )
                }
                
                Text(
                    text = title,
                    style = Theme.typography.header,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Theme.colors.textColorPrimary.enabled,
                )
            }
            CompositionLocalProvider(
                LocalContentColor provides Theme.colors.primary,
                LocalIconColor provides Theme.colors.primary,
            ) {
                trailingContent?.invoke()
            }
        }
        Divider()
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        Column(Modifier.background(Theme.colors.background)) {
            ListItemHeader(title = "Title")
            ListItemHeader(
                title = "Title",
                subtitle = "Subtitle",
            )
            ListItemHeader(title = "Title") {
                Text(text = "9 results")
            }
            ListItemHeader(
                title = "Title",
                subtitle = "Subtitle",
            ) {
                Text(text = "9 results")
            }
        }
    }
}