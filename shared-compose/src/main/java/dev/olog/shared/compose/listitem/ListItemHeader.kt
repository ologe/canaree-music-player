package dev.olog.shared.compose.listitem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import dev.olog.shared.compose.ThemePreviews
import dev.olog.shared.compose.component.Divider
import dev.olog.shared.compose.component.Text
import dev.olog.shared.compose.theme.CanareeTheme
import dev.olog.shared.compose.theme.Theme

@Composable
fun ListItemHeader(
    text: String,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    trailingContent: @Composable (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .padding(contentPadding)
            .padding(horizontal = Theme.spacing.mediumSmall)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = Theme.spacing.small)
        ) {
            Text(
                text = text,
                style = Theme.typography.header,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Theme.colors.textColorPrimary.enabled,
                modifier = Modifier.weight(1f),
            )
            trailingContent?.invoke()
        }
        Divider(
            modifier = Modifier
                .padding(vertical = Theme.spacing.small)
        )
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        Box(Modifier.background(Theme.colors.background)) {
            ListItemHeader(text = "Header")
            ListItemHeader(text = "Header") {
                Text(text = "9 results")
            }
        }
    }
}