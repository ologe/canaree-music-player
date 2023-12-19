package dev.olog.presentation.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.core.view.doOnLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.presentation.R
import dev.olog.presentation.widgets.fascroller.ScrollableItem
import dev.olog.shared.TextUtils
import dev.olog.shared.compose.ThemePreviews
import dev.olog.shared.compose.component.EmptyState
import dev.olog.shared.compose.component.WaveScroller
import dev.olog.shared.compose.extension.plus
import dev.olog.shared.compose.listitem.ListItemAlbum
import dev.olog.shared.compose.listitem.ListItemHeader
import dev.olog.shared.compose.listitem.ListItemPodcast
import dev.olog.shared.compose.listitem.ListItemShuffle
import dev.olog.shared.compose.listitem.ListItemTrack
import dev.olog.shared.compose.theme.CanareeTheme
import dev.olog.shared.compose.theme.Theme
import dev.olog.shared.widgets.scroller.WaveSideBarView
import kotlinx.coroutines.launch

@Composable
fun TabScreen(
    category: TabCategory,
    contentPadding: PaddingValues,
    onShuffleClick: () -> Unit,
    onPlayableClick: (MediaId) -> Unit,
    onAlbumClick: (MediaId) -> Unit,
    toDialog: (MediaId) -> Unit,
) {
    val viewModel = viewModel<TabFragmentViewModel>()
    val state by viewModel.observeState(category).collectAsState(null)

    TabContent(
        category = category,
        state = state ?: return,
        contentPadding = contentPadding,
        getCurrentSorting = { item ->
            val sort = viewModel.getSort(category)
            if (item is ScrollableItem) {
                return@TabContent item.getText(sort.type)
            }
            return@TabContent ""
        },
        onShuffleClick = onShuffleClick,
        onPlayableClick = onPlayableClick,
        onAlbumClick = onAlbumClick,
        toDialog = toDialog,
    )
}

@Composable
private fun TabContent(
    category: TabCategory,
    state: TabScreenState,
    contentPadding: PaddingValues,
    getCurrentSorting: (TabListItem) -> String,
    onShuffleClick: () -> Unit,
    onPlayableClick: (MediaId) -> Unit,
    onAlbumClick: (MediaId) -> Unit,
    toDialog: (MediaId) -> Unit,
) {
    val listState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(state.spanCount),
            contentPadding = contentPadding + computeAdditionalContentPadding(),
            horizontalArrangement = Theme.spacing.listHorizontalArrangement,
            verticalArrangement = Theme.spacing.listVerticalArrangement,
            modifier = Modifier.fillMaxSize(),
            state = listState,
        ) {
            items(
                items = state.items,
                span = LazyGridItemSpanScope::computeSpanCount,
            ) { item ->
                TabListItem(
                    item = item,
                    onShuffleClick = onShuffleClick,
                    onPlayableClick = onPlayableClick,
                    onAlbumClick = onAlbumClick,
                    toDialog = toDialog,
                )
            }
        }

        WaveScroller(
            letters = state.letters,
            modifier = Modifier.align(Alignment.CenterEnd),
            listener = LetterTouchListener(
                items = state.items,
                getCurrentSorting = getCurrentSorting,
                onPositionChange = {
                    coroutineScope.launch { listState.scrollToItem(it) }
                }
            ),
            showFade = !isRowList(category, state.spanCount)
        )

        if (state.items.isEmpty()) {
            EmptyState(
                text = category.asEmptyStateString(),
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}

private fun isRowList(category: TabCategory, spanCount: Int): Boolean {
    return spanCount == 1 &&
        category != TabCategory.PLAYLISTS &&
        category != TabCategory.PODCASTS_PLAYLIST
}

@Composable
private fun computeAdditionalContentPadding(): PaddingValues {
    return Theme.spacing.listContentPadding +
        PaddingValues(end = dimensionResource(R.dimen.tab_margin_end))
}

private fun LazyGridItemSpanScope.computeSpanCount(item: TabListItem): GridItemSpan {
    return when (item) {
        is TabListItem.Header,
        is TabListItem.List -> GridItemSpan(maxLineSpan)
        else -> GridItemSpan(1)
    }
}

@Composable
private fun TabListItem(
    item: TabListItem,
    onShuffleClick: () -> Unit,
    onPlayableClick: (MediaId) -> Unit,
    onAlbumClick: (MediaId) -> Unit,
    toDialog: (MediaId) -> Unit,
) {
    when (item) {
        is TabListItem.Track -> ListItemTrack(
            mediaId = item.mediaId,
            title = item.title,
            subtitle = item.subtitle,
            onClick = { onPlayableClick(item.mediaId) },
            onLongClick = { toDialog(item.mediaId) },
        )

        is TabListItem.Podcast -> {
            ListItemPodcast(
                mediaId = item.mediaId,
                title = item.title,
                subtitle = item.subtitle,
                duration = "${item.duration.inWholeMinutes}m",
                onClick = { onPlayableClick(item.mediaId) },
                onLongClick = { toDialog(item.mediaId) },
            )
        }

        is TabListItem.Album -> {
            if (item.asRow) {
                ListItemTrack(
                    mediaId = item.mediaId,
                    title = item.title,
                    subtitle = item.subtitle.orEmpty(),
                    onClick = { onAlbumClick(item.mediaId) },
                    onLongClick = { toDialog(item.mediaId) },
                )
            } else {
                ListItemAlbum(
                    mediaId = item.mediaId,
                    title = item.title,
                    subtitle = item.subtitle,
                    onClick = { onAlbumClick(item.mediaId) },
                    onLongClick = { toDialog(item.mediaId) },
                )
            }
        }

        is TabListItem.Header -> {
            ListItemHeader(item.text)
        }

        is TabListItem.List -> {
            LazyRow(
                horizontalArrangement = Theme.spacing.listHorizontalArrangement,
                modifier = Modifier.fillScreenWidth(),
                contentPadding = computeAdditionalContentPadding(),
            ) {
                items(item.items) { nestedItem ->
                    Box(Modifier.width(dimensionResource(id = R.dimen.item_tab_album_last_player_width))) {
                        ListItemAlbum(
                            mediaId = nestedItem.mediaId,
                            title = nestedItem.title,
                            subtitle = nestedItem.subtitle,
                            onClick = { onAlbumClick(nestedItem.mediaId) },
                            onLongClick = { toDialog(nestedItem.mediaId) },
                        )
                    }
                }
            }
        }

        is TabListItem.Shuffle -> {
            ListItemShuffle(onClick = onShuffleClick)
        }
    }
}

// allows to draw outside of list bounds
private fun Modifier.fillScreenWidth() = composed {
    val view = LocalView.current
    var width by remember { mutableIntStateOf(-1) }
    LaunchedEffect(view) {
        view.doOnLayout {
            width = it.width
        }
    }
    Modifier
        graphicsLayer {
            translationX = 12.dp.toPx() // TODO seems a random value but it works, find what it is
        }
        .layout { measurable, constraints ->
            val c = if (width == -1) constraints else constraints.copy(
                minWidth = width,
                maxWidth = width
            )
            val placeable = measurable.measure(c)
            layout(width, placeable.height) {
                placeable.place(0, 0)
            }
        }
}

@Stable
private class LetterTouchListener(
    private val items: List<TabListItem>,
    private val getCurrentSorting: (TabListItem) -> String,
    private val onPositionChange: (Int) -> Unit
) : WaveSideBarView.OnTouchLetterChangeListener {
    override fun onLetterChange(letter: String?) {
        val position = when (letter) {
            TextUtils.MIDDLE_DOT -> -1
            "#" -> items.indexOfFirst {
                if (it !is ScrollableItem) {
                    false
                } else {
                    val sorting = getCurrentSorting(it)
                    if (sorting.isBlank()) false
                    else sorting[0].uppercase().isDigitsOnly()
                }
            }

            "?" -> items.indexOfFirst {
                if (it !is ScrollableItem) {
                    false
                } else {
                    val sorting = getCurrentSorting(it)
                    if (sorting.isBlank()) false
                    else sorting[0].uppercase() > "Z"
                }
            }

            else -> items.indexOfFirst {
                if (it !is ScrollableItem) {
                    false
                } else {
                    val sorting = getCurrentSorting(it)
                    if (sorting.isBlank()) false
                    else sorting[0].uppercase() == letter
                }
            }
        }
        if (position != -1) {
            onPositionChange(position)
        }
    }
}


@ThemePreviews
@Composable
private fun Preview() {
    CanareeTheme {
        Box(Modifier.background(Theme.colors.background)) {
            TabContent(
                category = TabCategory.FOLDERS,
                state = TabScreenState(
                    items = listOf(
                        TabListItem.Shuffle,
                        TabListItem.List(
                            (0..10).map {
                                TabListItem.Album.NonScrollable(
                                    MediaId.createCategoryValue(MediaIdCategory.ALBUMS, "$it"),
                                    title = "title $it",
                                    subtitle = null,
                                )
                            }
                        ),
                    ),
                    letters = emptyList(),
                    spanCount = 2,
                ),
                contentPadding = PaddingValues(),
                getCurrentSorting = { "" },
                onShuffleClick = {},
                onPlayableClick = {},
                onAlbumClick = {},
                toDialog = {},
            )
        }
    }
}