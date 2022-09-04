package dev.olog.feature.search.widget

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import dev.olog.compose.gesture.CircularSwipeToDismiss
import dev.olog.compose.gesture.clickable
import dev.olog.core.MediaId
import dev.olog.feature.library.api.widget.MediaAlbum
import dev.olog.feature.library.api.widget.MediaTrack
import dev.olog.feature.search.model.SearchItem
import dev.olog.feature.search.model.SearchState
import localization.R

@Suppress("FunctionName")
fun LazyListScope.SearchList(
    state: SearchState.Items,
    onPlayableClick: (MediaId) -> Unit,
    onNonPlayableClick: (MediaId) -> Unit,
    onItemLongClick: (MediaId) -> Unit,
    onPlayNext: (MediaId) -> Unit,
    onDelete: (MediaId) -> Unit,
) {
    horizontalList(
        header = R.string.common_playlists,
        items = state.playlists,
        onItemClick = onNonPlayableClick,
        onItemLongClick = onItemLongClick,
    )
    horizontalList(
        header = R.string.common_genres,
        items = state.genres,
        onItemClick = onNonPlayableClick,
        onItemLongClick = onItemLongClick,
    )
    horizontalList(
        header = R.string.common_artists,
        items = state.artists,
        onItemClick = onNonPlayableClick,
        onItemLongClick = onItemLongClick,
    )
    horizontalList(
        header = R.string.common_albums,
        items = state.albums,
        onItemClick = onNonPlayableClick,
        onItemLongClick = onItemLongClick,
    )

    if (state.tracks.isNotEmpty()) {
        item {
            SearchHeader(
                stringRes = R.string.common_tracks,
                itemsCount = state.tracks.size,
            )
        }
    }

    items(
        items = state.tracks,
        key = { item -> item.mediaId }
    ) { item ->

        CircularSwipeToDismiss(
            onPlayNext = { onPlayNext(item.mediaId); true },
            onDelete = { onDelete(item.mediaId); false },
        ) {
            MediaTrack(
                mediaId = item.mediaId,
                title = item.title,
                subtitle = item.subtitle.orEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        onClick = { onPlayableClick(item.mediaId) },
                        onLongClick = { onItemLongClick(item.mediaId) }
                    ),
            )
        }
    }
}

private fun LazyListScope.horizontalList(
    @StringRes header: Int,
    items: List<SearchItem>,
    onItemClick: (MediaId) -> Unit,
    onItemLongClick: (MediaId) -> Unit,
) {
    if (items.isEmpty()) {
        return
    }

    item(key = header) {
        Column {
            SearchHeader(
                stringRes = header,
                itemsCount = items.size,
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = dimensionResource(dev.olog.ui.R.dimen.screen_margin)),
            ) {
                itemsIndexed(
                    items = items,
                    key = { _, item -> item.mediaId }
                ) { index, item ->
                    MediaAlbum(
                        mediaId = item.mediaId,
                        title = item.title,
                        subtitle = item.subtitle,
                        modifier = Modifier
                            .padding(
                                start = if (index == 0) 0.dp else 4.dp,
                                end = if (index == items.lastIndex) 0.dp else 4.dp,
                            )
                            .width(100.dp)
                            .clickable(
                                onClick = { onItemClick(item.mediaId) },
                                onLongClick = { onItemLongClick(item.mediaId) },
                                withRipple = false,
                            )
                    )
                }
            }
        }
    }
}