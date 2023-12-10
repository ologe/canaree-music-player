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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import dev.olog.core.MediaId
import dev.olog.presentation.R
import dev.olog.shared.compose.ThemePreviews
import dev.olog.shared.compose.component.IconButton
import dev.olog.shared.compose.listitem.ListItemAlbum
import dev.olog.shared.compose.listitem.ListItemHeader
import dev.olog.shared.compose.theme.CanareeTheme
import dev.olog.shared.compose.theme.LocalIconColor
import dev.olog.shared.compose.theme.Theme

private const val MaxItemsToShow = 10

@Composable
fun DetailRelatedArtistsContent(
    relatedArtists: DetailFragmentItem.RelatedArtists,
    onClick: (MediaId) -> Unit,
    onLongClick: (MediaId) -> Unit,
    onSeeAllClick: () -> Unit,
) {
    if (relatedArtists.items.isEmpty()) {
        return
    }

    Column {
        val size = relatedArtists.items.size
        ListItemHeader(stringResource(R.string.detail_related_artists)) {
            if (size > MaxItemsToShow) {
                CompositionLocalProvider(LocalIconColor provides Theme.colors.iconColor) {
                    IconButton(
                        drawableRes = R.drawable.vd_arrow_forward,
                        onClick = onSeeAllClick,
                    )
                }
            }
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = Theme.spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
        ) {
            items(relatedArtists.items.take(MaxItemsToShow)) { item ->
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
            DetailRelatedArtistsContent(
                relatedArtists = DetailFragmentItem.RelatedArtists(
                    items = (0..6).map {
                        DetailRelatedArtistItem(
                            mediaId = MediaId.songId(it.toLong()),
                            title = "title $it",
                            subtitle = "subtitle $it",
                        )
                    }
                ),
                onClick = {},
                onLongClick = {},
                onSeeAllClick = {}
            )

            DetailRelatedArtistsContent(
                relatedArtists = DetailFragmentItem.RelatedArtists(
                    items = (0..20).map {
                        DetailRelatedArtistItem(
                            mediaId = MediaId.songId(it.toLong()),
                            title = "title $it",
                            subtitle = "subtitle $it",
                        )
                    }
                ),
                onClick = {},
                onLongClick = {},
                onSeeAllClick = {}
            )
        }
    }
}