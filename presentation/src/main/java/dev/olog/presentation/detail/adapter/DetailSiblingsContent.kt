package dev.olog.presentation.detail.adapter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringArrayResource
import dev.olog.core.MediaId
import dev.olog.presentation.R
import dev.olog.shared.compose.ThemePreviews
import dev.olog.shared.compose.listitem.ListItemAlbum
import dev.olog.shared.compose.listitem.ListItemHeader
import dev.olog.shared.compose.theme.CanareeTheme
import dev.olog.shared.compose.theme.Theme

@Composable
fun DetailSiblingsContent(
    siblings: DetailFragmentItem.Siblings,
    onClick: (MediaId) -> Unit,
    onLongClick: (MediaId) -> Unit,
) {
    if (siblings.items.isEmpty()) {
        return
    }

    Column {
        ListItemHeader(siblings.header)
        LazyRow(
            contentPadding = PaddingValues(horizontal = Theme.spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
        ) {
            items(siblings.items) { item ->
                ListItemAlbum(
                    mediaId = item.mediaId,
                    title = item.title,
                    subtitle = item.subtitle,
                    modifier = Modifier.width(dimensionResource(R.dimen.item_tab_album_last_player_width)),
                    onClick = { onClick(item.mediaId) },
                    onLongClick = { onLongClick(item.mediaId) }
                )
            }
        }
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Theme.colors.background)
        ) {
            DetailSiblingsContent(
                siblings = DetailFragmentItem.Siblings(
                    header = stringArrayResource(id = R.array.detail_album_header)[0],
                    items = (0..10).map {
                        DetailSiblingItem(
                            mediaId = MediaId.songId(it.toLong()),
                            title = "title $it",
                            subtitle = "subtitle $it",
                        )
                    }
                ),
                onClick = {},
                onLongClick = {},
            )
        }
    }
}