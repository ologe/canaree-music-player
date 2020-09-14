package dev.olog.shared.components.item

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import androidx.ui.tooling.preview.PreviewParameter
import dev.olog.shared.components.R
import dev.olog.shared.components.sample.DarkModePreviewProviders
import dev.olog.shared.components.theme.CanareeTheme

@Composable
@Preview
private fun CanareeToolbarPreview(
    @PreviewParameter(DarkModePreviewProviders::class) isDarkTheme: Boolean
) {
    CanareeTheme(darkTheme = isDarkTheme) {
        Surface {
            ListItemShuffle()
        }
    }
}

@Composable
fun ListItemShuffle(
    startPadding: Dp = 16.dp,
    endPadding: Dp = 16.dp,
    onClick: (() -> Unit) = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                start = startPadding,
                end = endPadding,
                top = 8.dp,
                bottom = 8.dp
            ),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalGravity = Alignment.CenterVertically
    ) {
        Icon(
            asset = Icons.Rounded.Shuffle,
            modifier = Modifier.preferredSize(52.dp)
        )
        Text(text = stringResource(id = R.string.common_shuffle), style = MaterialTheme.typography.body1)
    }
}