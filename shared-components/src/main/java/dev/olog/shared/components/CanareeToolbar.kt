package dev.olog.shared.components

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import dev.olog.shared.components.sample.DarkModePreviewProviders
import dev.olog.shared.components.theme.CanareeTheme

internal val ToolbarElevation = 2.dp

@Composable
@Preview
private fun CanareeToolbarPreview(
    @PreviewParameter(DarkModePreviewProviders::class) isDarkTheme: Boolean
) {
    CanareeTheme(darkTheme = isDarkTheme) {
        CanareeToolbar {
            IconButton(onClick = {}) {
                Icon(asset = Icons.Rounded.Search)
            }
            IconButton(onClick = {}) {
                Icon(asset = Icons.Rounded.MoreVert)
            }
        }
    }
}

@Composable
fun CanareeToolbar(
    title: String = "Canaree",
    icons: @Composable (() -> Unit)? = null
) {
    Surface(
        color = MaterialTheme.colors.surface,
        elevation = if (isSystemInDarkTheme()) 0.dp else ToolbarElevation
    ) {
        Row(Modifier
            .fillMaxWidth()
            .preferredHeight(64.dp)
            .padding(start = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalGravity = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.h4,
                modifier = Modifier.weight(1f)
            )
            icons?.invoke()
        }
    }
}
