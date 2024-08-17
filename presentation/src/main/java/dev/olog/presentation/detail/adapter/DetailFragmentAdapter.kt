package dev.olog.presentation.detail.adapter


import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import dev.olog.core.MediaId
import dev.olog.core.entity.sort.SortEntity
import dev.olog.core.entity.sort.SortType
import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.ComposeListAdapter
import dev.olog.presentation.base.adapter.ComposeViewHolder
import dev.olog.presentation.base.adapter.InteractableItem
import dev.olog.presentation.base.drag.TouchableAdapter
import dev.olog.presentation.detail.DetailFragmentHeaders.Companion.NESTED_SPAN_COUNT
import dev.olog.presentation.detail.DetailSortDialog
import dev.olog.presentation.detail.ui.DetailContent
import dev.olog.presentation.detail.ui.DetailDurationFooter
import dev.olog.presentation.detail.ui.DetailRecentlyAddedHeader
import dev.olog.presentation.detail.ui.DetailRelatedArtistsHeader
import dev.olog.presentation.detail.ui.DetailSongsHeader
import dev.olog.shared.compose.Theme
import dev.olog.shared.compose.component.DragHandle
import dev.olog.shared.compose.component.LazyHorizontalGridFix
import dev.olog.shared.compose.list.ListItemAlbum
import dev.olog.shared.compose.list.ListItemHeader
import dev.olog.shared.compose.list.ListItemPlaceholder
import dev.olog.shared.compose.list.ListItemShuffle
import dev.olog.shared.compose.list.ListItemSong
import dev.olog.shared.exhaustive

internal class DetailFragmentAdapter(
    private val onShuffleClick: (MediaId) -> Unit,
    private val onSongClick: (MediaId) -> Unit,
    private val onMostPlayedClick: (MediaId) -> Unit,
    private val onRecentlyAddedClick: (MediaId) -> Unit,
    private val onAlbumClick: (MediaId) -> Unit,
    private val onLongClick: (MediaId, View) -> Unit,
    private val goToRelatedArtists: (MediaId) -> Unit,
    private val goToRecentlyAdded: (MediaId) -> Unit,
    private val onAddToPlayNext: (MediaId) -> Unit,
    private val onAddMove: (from: Int, to: Int) -> Unit,
    private val onProcessMove: () -> Unit,
    private val onRemoveFromPlaylist: (MediaId, idInPlaylist: Int) -> Unit,
    private val onUpdateSort: (SortType) -> Unit,
    private val toggleSortDirection: () -> Unit,
    private val onStartDrag: (vh: ViewHolder) -> Unit,
) : ComposeListAdapter<DetailItem>(), TouchableAdapter {

    private val headers: Int
        get() = currentList.indexOfFirst { it is DetailItem.Song }

    override fun bind(holder: ComposeViewHolder, item: DetailItem, position: Int) {
        holder.setContent {
            when (item) {
                is DetailItem.DetailHeader -> {
                    DetailContent(
                        mediaId = item.mediaId,
                        title = item.title,
                        subtitle = item.subtitle,
                        biography = item.biography,
                        itemView = holder.itemView,
                    )
                }
                is DetailItem.Header -> ListItemHeader(item.title)
                is DetailItem.Shuffle -> ListItemShuffle(
                    showDivider = false,
                    onClick = { onShuffleClick(item.mediaId) },
                )
                is DetailItem.HeaderSongs -> {
//                if (viewModel.showSortByTutorialIfNeverShown()) {
//                    TutorialTapTarget.sortBy(sortText, sortImage)
//                }
                    DetailSongsHeader(
                        sort = item.sort,
                        onSortClick = {
                            DetailSortDialog().show(
                                view = holder.itemView,
                                mediaId = item.mediaId,
                                sortType = item.sort.type,
                                updateUseCase = onUpdateSort,
                            )
                        },
                        onSortDirectionClick = toggleSortDirection
                    )
                }
                is DetailItem.Footer -> DetailDurationFooter(item.title)
                is DetailItem.Song -> SongContent(item, holder)
                is DetailItem.MostPlayedList -> {
                    // TODO ui jumps on first item added
                    HorizontalList(item.items) { nestedItem ->
                        ListItemSong(
                            mediaId = nestedItem.mediaId,
                            title = nestedItem.title,
                            subtitle = nestedItem.subtitle,
                            indexContent = { Text(nestedItem.position) },
                            modifier = Modifier.fillMaxSize(),
                            onClick = { onMostPlayedClick(nestedItem.mediaId) },
                            onLongClick = { onLongClick(nestedItem.mediaId, holder.itemView) }
                        )
                    }
                }
                is DetailItem.HeaderRecentlyAdded -> DetailRecentlyAddedHeader(
                    itemsCount = item.itemsCount,
                    showSeeAll = item.showSeeAll,
                    onClick = { goToRecentlyAdded(item.mediaId) }
                )
                is DetailItem.RecentlyAddedList -> {
                    HorizontalList(item.items) { nestedItem ->
                        ListItemSong(
                            mediaId = nestedItem.mediaId,
                            title = nestedItem.title,
                            subtitle = nestedItem.subtitle,
                            modifier = Modifier.fillMaxSize(),
                            onClick = { onRecentlyAddedClick(nestedItem.mediaId) },
                            onLongClick = { onLongClick(nestedItem.mediaId, holder.itemView) }
                        )
                    }
                }
                is DetailItem.HeaderRelatedArtists -> DetailRelatedArtistsHeader(
                    showSeeAll = item.showSeeAll,
                    onClick = { goToRelatedArtists(item.mediaId) }
                )
                is DetailItem.RelatedArtistsList -> {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = dimensionResource(R.dimen.detail_album_margin_horizontal))
                    ) {
                        items(item.items) { nestedItem ->
                            ListItemAlbum(
                                mediaId = nestedItem.mediaId,
                                title = nestedItem.title,
                                subtitle = nestedItem.subtitle,
                                modifier = Modifier.width(dimensionResource(R.dimen.item_tab_album_last_player_width)),
                                onClick = { onAlbumClick(nestedItem.mediaId) },
                                onLongClick = { onLongClick(nestedItem.mediaId, holder.itemView) }
                            )
                        }
                    }
                }
                is DetailItem.HeaderSiblings -> ListItemHeader(item.title)
                is DetailItem.SiblingsList -> {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = dimensionResource(R.dimen.detail_album_margin_horizontal))
                    ) {
                        items(item.items) { nestedItem ->
                            ListItemAlbum(
                                mediaId = nestedItem.mediaId,
                                title = nestedItem.title,
                                subtitle = nestedItem.subtitle,
                                modifier = Modifier.width(dimensionResource(R.dimen.item_tab_album_last_player_width)),
                                onClick = { onAlbumClick(nestedItem.mediaId) },
                                onLongClick = { onLongClick(nestedItem.mediaId, holder.itemView) }
                            )
                        }
                    }
                }
            }.exhaustive
        }
    }

    @Composable
    private fun <T> HorizontalList(
        items: List<T>,
        content: @Composable (T) -> Unit,
    ) {
        LazyHorizontalGridFix(
            itemContent = { ListItemPlaceholder() }
        ) { itemSize ->
            val rows = items.size.coerceAtMost(NESTED_SPAN_COUNT)
            // this seems mathematically incorrect but uses int division, e.g
            // 6 (size) / 4(span) = 1 * 4(span) = 4
            val previousCellCount = items.size / NESTED_SPAN_COUNT * NESTED_SPAN_COUNT
            val allCellsCount = previousCellCount + NESTED_SPAN_COUNT
            LazyHorizontalGrid(
                rows = GridCells.Fixed(rows),
                modifier = Modifier.height(itemSize.height * rows),
                contentPadding = PaddingValues(end = 12.dp), // TODO
            ) {
                itemsIndexed(items) { index, nestedItem ->
                    val endPadding = if (allCellsCount - index <= previousCellCount) {
                        dimensionResource(R.dimen.detail_horizontal_list_margin_end) / 2
                    } else {
                        dimensionResource(R.dimen.detail_horizontal_list_margin_end)
                    }
                    Box(
                        Modifier
                            .height(itemSize.height)
                            .width(itemSize.width - endPadding)
                    ) {
                        content(nestedItem)
                    }
                }
            }
        }
    }

    @Composable
    private fun SongContent(
        item: DetailItem.Song,
        holder: ViewHolder,
    ) {
        when (item.mode) {
            is DetailSongMode.Album -> ListItemSong(
                leadingContent = {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = item.mode.trackNumber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Theme.colors.textColorPrimary,
                        )
                    }
                },
                title = item.title,
                subtitle = item.subtitle,
                onClick = { onSongClick(item.mediaId) },
                onLongClick = { onLongClick(item.mediaId, holder.itemView) }
            )
            is DetailSongMode.Folder -> ListItemSong(
                mediaId = item.mediaId,
                title = item.title,
                subtitle = item.subtitle,
                indexContent = { Text(text = item.mode.trackNumber) },
                onClick = { onSongClick(item.mediaId) },
                onLongClick = { onLongClick(item.mediaId, holder.itemView) }
            )
            is DetailSongMode.Playlist -> ListItemSong(
                mediaId = item.mediaId,
                title = item.title,
                subtitle = item.subtitle,
                onClick = { onSongClick(item.mediaId) },
                onLongClick = { onLongClick(item.mediaId, holder.itemView) },
                trailingContent = if (item.mode.showDragHandle) {{
                    DragHandle {
                        onStartDrag(holder)
                    }
                }} else null
            )
            null -> {
                ListItemSong(
                    mediaId = item.mediaId,
                    title = item.title,
                    subtitle = item.subtitle,
                    onClick = { onSongClick(item.mediaId) },
                    onLongClick = { onLongClick(item.mediaId, holder.itemView) }
                )
            }
        }
    }

    // TODO very likely crash for inconsistency
    override fun onClearView() {
        commitChange {
            onProcessMove()
        }
    }

    override fun onMoved(from: Int, to: Int) {
        val realFrom = from - headers
        val realTo = to - headers
        moveItem(from, to)
        onAddMove(realFrom, realTo) // TODO check
    }

    // TODO crash for inconsistency
    override fun onSwipedRight(viewHolder: ViewHolder) {
        val item = getItem(viewHolder.adapterPosition)
        if (item !is DetailItem.Song) return
        if (item.mode !is DetailSongMode.Playlist) return

        removeItem(viewHolder.adapterPosition)
        commitChange {
            onRemoveFromPlaylist(item.mediaId, item.mode.idInPlaylist)
        }
    }

    override fun afterSwipeRight(viewHolder: ViewHolder) {

    }

    override fun onSwipedLeft(viewHolder: ViewHolder) {
        val item = getItem(viewHolder.adapterPosition)
        if (item is DetailItem.Song) {
            onAddToPlayNext(item.mediaId)
        }
    }

    override fun afterSwipeLeft(viewHolder: ViewHolder) {
        notifyItemChanged(viewHolder.adapterPosition)
    }


}

@Stable
sealed interface DetailItem {

    @Stable
    data class DetailHeader(
        val mediaId: MediaId,
        val title: String,
        val subtitle: String,
        val biography: String?
    ) : DetailItem

    @Stable
    data class Shuffle(val mediaId: MediaId): DetailItem

    @Stable
    data class MostPlayedList(val items: List<MostPlayed>): DetailItem

    @Stable
    data class RecentlyAddedList(val items: List<RecentlyAdded>): DetailItem

    @Stable
    data class SiblingsList(val items: List<Album>): DetailItem

    @Stable
    data class RelatedArtistsList(val items: List<Album>): DetailItem

    @Stable
    data class Album(
        val mediaId: MediaId,
        val title: String,
        val subtitle: String,
    )

    @Stable
    data class Song(
        val mediaId: MediaId,
        val title: String,
        val subtitle: String?,
        val mode: DetailSongMode?,
    ): DetailItem, InteractableItem

    @Stable
    data class MostPlayed(
        val mediaId: MediaId,
        val title: String,
        val subtitle: String,
        val position: String,
    )

    @Stable
    data class RecentlyAdded(
        val mediaId: MediaId,
        val title: String,
        val subtitle: String,
    )

    @Stable
    data class Header(val title: String): DetailItem

    @Stable
    data class HeaderRelatedArtists(
        val mediaId: MediaId,
        val showSeeAll: Boolean
    ): DetailItem

    @Stable
    data class HeaderRecentlyAdded(
        val mediaId: MediaId,
        val itemsCount: Int,
        val showSeeAll: Boolean,
    ): DetailItem

    @Stable
    data class HeaderSiblings(val title: String): DetailItem

    @Stable
    data class HeaderSongs(
        val mediaId: MediaId,
        val sort: SortEntity,
    ): DetailItem

    @Stable
    data class Footer(val title: String): DetailItem

}

@Stable
sealed interface DetailSongMode {

    @Stable
    data class Playlist(
        val idInPlaylist: Int,
        val showDragHandle: Boolean,
    ): DetailSongMode

    @Stable
    data class Album(val trackNumber: String): DetailSongMode

    @Stable
    data class Folder(val trackNumber: String): DetailSongMode

}