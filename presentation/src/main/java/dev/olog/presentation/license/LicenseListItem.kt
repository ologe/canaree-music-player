package dev.olog.presentation.license

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import dev.olog.shared.compose.ThemePreviews
import dev.olog.shared.compose.component.Text
import dev.olog.shared.compose.theme.CanareeTheme
import dev.olog.shared.compose.theme.Theme

@Composable
fun LicenseListItem(
    name: String,
    url: String,
    license: String,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    Column(
        modifier = modifier
            .padding(Theme.spacing.medium)
    ) {
        Text(
            text = name,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = Theme.typography.headline,
            color = Theme.colors.textColorPrimary.enabled,
        )
        Text(
            text = url,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Theme.spacing.small)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = { uriHandler.openUri(url) }
                ),
            textAlign = TextAlign.Center,
            color = Theme.colors.accent,
        )
        // TODO check newlines
        Text(
            text = license,
            color = Theme.colors.textColorSecondary.enabled,
        )
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        Box(Modifier.background(Theme.colors.background)) {
            LicenseListItem(
                name = "Jetpack Compose",
                url = "https://developer.android.com/jetpack/compose",
                license = LoremIpsum(50).values.joinToString()
            )
        }
    }
}