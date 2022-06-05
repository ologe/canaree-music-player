package dev.olog.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.olog.compose.theme.CanareeTheme

@Composable
fun CanareeToolbar(
    modifier: Modifier = Modifier,
    text: @Composable () -> Unit,
    icons: (@Composable RowScope.() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colors.surface)
            .padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(LocalContentColor provides contentColorFor(MaterialTheme.colors.surface)) {
            text()
            Spacer(Modifier.weight(1f))
            icons?.invoke(this)
        }
    }
}

@Composable
fun CanareeToolbar(
    text: String,
    modifier: Modifier = Modifier,
    icons: (@Composable RowScope.() -> Unit)? = null,
) {
    CanareeToolbar(
        modifier = modifier,
        text = { ToolbarTitle(text = text) },
        icons = icons
    )
}

@ThemePreviews
@Composable
private fun PreviewNoIcons() {
    CanareeTheme {
        CanareeToolbar(
            modifier = Modifier.fillMaxWidth(),
            text = "Title",
        )
    }
}

@Preview
@Composable
private fun PreviewIcons() {
    CanareeTheme {
        CanareeToolbar(
            modifier = Modifier.fillMaxWidth(),
            text = "Title",
            icons = {
                CanareeIconButton(imageVector = Icons.Default.Search) {}
                CanareeIconButton(imageVector = Icons.Default.MoreVert) {}
            }
        )
    }
}

@Preview
@Composable
private fun PreviewLongTitleIcons() {
    // todo is pushing buttons out
    CanareeTheme {
        CanareeToolbar(
            modifier = Modifier.fillMaxWidth(),
            text = "LoremIpsum ipsum",
            icons = {
                CanareeIconButton(imageVector = Icons.Default.Search) {}
                CanareeIconButton(imageVector = Icons.Default.MoreVert) {}
            }
        )
    }
}

@Composable
fun ToolbarTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.h5,
        maxLines = 1,
    )
}