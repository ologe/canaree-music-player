package dev.olog.shared.compose.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.olog.shared.compose.CanareeTheme
import dev.olog.shared.compose.R
import dev.olog.shared.compose.ThemePreviews
import dev.olog.shared.compose.component.DottedDivider
import dev.olog.shared.compose.list.internal.ListItemRow

@Composable
fun ListItemShuffle(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier.padding(bottom = 8.dp)
    ) {
        ListItemRow(
            leadingContent = {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.Shuffle,
                        contentDescription = null,
                    )
                }
            },
            title = {
                Text(
                    text = stringResource(R.string.common_shuffle),
                    fontSize = with(LocalDensity.current) { dimensionResource(R.dimen.item_shuffle_text_size).toSp() }
                )
            },
            subtitle = null,
            trailingContent = null,
            onClick = onClick,
            onLongClick = null,
            modifier = Modifier,
        )
        DottedDivider(
            Modifier.padding(horizontal = dimensionResource(R.dimen.item_song_cover_margin_start))
        )
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        Box(Modifier.background(MaterialTheme.colors.background)) {
            ListItemShuffle {}
        }
    }
}