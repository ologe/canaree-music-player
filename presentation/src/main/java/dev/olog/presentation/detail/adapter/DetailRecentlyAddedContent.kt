package dev.olog.presentation.detail.adapter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dev.olog.core.MediaId
import dev.olog.presentation.R
import dev.olog.shared.compose.component.IconButton
import dev.olog.shared.compose.listitem.ListItemHeader
import dev.olog.shared.compose.listitem.ListItemTrack
import dev.olog.shared.compose.theme.CanareeTheme
import dev.olog.shared.compose.theme.LocalIconColor
import dev.olog.shared.compose.theme.Theme

private const val Rows = 4
private const val MaxItemsToShow = Rows * 4

@Composable
fun DetailRecentlyAddedContent(
    recentlyAdded: DetailFragmentItem.RecentlyAdded,
    onClick: (MediaId) -> Unit,
    onLongClick: (MediaId) -> Unit,
    onSeeAllClick: () -> Unit,
) {
    if (recentlyAdded.items.isEmpty()) {
        return
    }

    Column {
        val size = recentlyAdded.items.size
        ListItemHeader(
            title = stringResource(id = R.string.detail_recently_added),
            subtitle = pluralStringResource(R.plurals.detail_xx_new_songs, size, size)
        ) {
            if (recentlyAdded.items.size > MaxItemsToShow) {
                CompositionLocalProvider(LocalIconColor provides Theme.colors.iconColor) {
                    IconButton(
                        drawableRes = R.drawable.vd_arrow_forward,
                        onClick = onSeeAllClick,
                    )
                }
            }
        }
        DetailLazyHorizontalGrid(
            items = recentlyAdded.items.take(MaxItemsToShow),
            maxRows = Rows,
        ) { item ->
            ListItemTrack(
                mediaId = item.mediaId,
                title = item.title,
                subtitle = item.subtitle,
                modifier = Modifier.fillMaxWidth(),
                onClick = { onClick(item.mediaId) },
                onLongClick = { onLongClick(item.mediaId) },
                trailingContent = {
                    IconButton(
                        drawableRes = R.drawable.vd_more,
                        onClick = { onLongClick(item.mediaId) }
                    )
                }
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    CanareeTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Theme.colors.background)
        ) {
            DetailRecentlyAddedContent(
                DetailFragmentItem.RecentlyAdded(
                    items = (0..5).map {
                        DetailRecentlyAddedItem(
                            mediaId = MediaId.songId(it.toLong()),
                            title = "title $it",
                            subtitle = "subtitle $it",
                        )
                    },
                ),
                onClick = {},
                onLongClick = {},
                onSeeAllClick = {}
            )

            DetailRecentlyAddedContent(
                DetailFragmentItem.RecentlyAdded(
                    items = (0..40).map {
                        DetailRecentlyAddedItem(
                            mediaId = MediaId.songId(it.toLong()),
                            title = "title $it",
                            subtitle = "subtitle $it",
                        )
                    },
                ),
                onClick = {},
                onLongClick = {},
                onSeeAllClick = {}
            )
        }
    }
}