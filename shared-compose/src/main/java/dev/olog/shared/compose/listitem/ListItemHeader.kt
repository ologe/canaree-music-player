package dev.olog.shared.compose.listitem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
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
    paddingValues: PaddingValues = PaddingValues(),
) {
    Column(
        modifier = modifier
            .padding(paddingValues)
            .padding(horizontal = Theme.spacing.mediumSmall)
    ) {
        Text(
            text = text,
            style = Theme.typography.header,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = Theme.colors.textColorPrimary.enabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = Theme.spacing.small,
                ),
        )
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
        }
    }
}