package dev.olog.feature.search.preview

import androidx.compose.runtime.Composable
import dev.olog.compose.CombinedPreviews
import dev.olog.compose.theme.CanareeTheme
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.feature.search.SearchContent
import dev.olog.feature.search.model.SearchRecentItem
import dev.olog.feature.search.model.SearchState

@CombinedPreviews
@Composable
private fun Preview() {
    CanareeTheme {
        SearchContent(
            data = SearchState.Recents(
                items = listOf(
                    MediaIdCategory.FOLDERS,
                    MediaIdCategory.PLAYLISTS,
                    MediaIdCategory.SONGS,
                    MediaIdCategory.ALBUMS,
                    MediaIdCategory.ARTISTS,
                    MediaIdCategory.GENRES,
                ).mapIndexed { index, category ->
                    val mediaId = if (category == MediaIdCategory.SONGS) {
                        MediaId.songId(index.toLong())
                    } else {
                        MediaId.createCategoryValue(category, index.toString())
                    }
                    SearchRecentItem(
                        mediaId = mediaId,
                        title = "title",
                        subtitle = "subtitle",
                        isPlayable = mediaId.isLeaf,
                        isPodcast = mediaId.isAnyPodcast
                    )
                }
            ),
            query = "",
            onQueryChange = { },
            onQueryClear = { },
            onBubbleClick = { },
            onMoreClick = { },
            onPlayableClick = { },
            onItemLongClick = { },
            onNonPlayableClick = { },
            onClearItemClick = { },
            onClearAllClick = { },
            onPlayNext = { },
            onDelete = { },
        )
    }
}