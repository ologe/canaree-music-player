package dev.olog.feature.search.preview

import androidx.compose.runtime.Composable
import dev.olog.compose.CombinedPreviews
import dev.olog.compose.theme.CanareeTheme
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.feature.search.SearchContent
import dev.olog.feature.search.model.SearchItem
import dev.olog.feature.search.model.SearchState

@CombinedPreviews
@Composable
private fun Preview() {
    fun createData(category: MediaIdCategory): List<SearchItem> {
        return (0..category.ordinal).mapIndexed { index, i ->
            val mediaId = if (category == MediaIdCategory.SONGS) {
                MediaId.songId(index.toLong())
            } else {
                MediaId.createCategoryValue(category, index.toString())
            }
            SearchItem(
                mediaId = mediaId,
                title = "title",
                subtitle = "subtitle",
                isPodcast = mediaId.isAnyPodcast,
            )
        }
    }

    CanareeTheme {
        SearchContent(
            data = SearchState.Items(
                playlists = createData(MediaIdCategory.PLAYLISTS),
                albums = createData(MediaIdCategory.ALBUMS),
                artists = createData(MediaIdCategory.ARTISTS),
                genres = createData(MediaIdCategory.GENRES),
                tracks = createData(MediaIdCategory.SONGS),
            ),
            query = "test",
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