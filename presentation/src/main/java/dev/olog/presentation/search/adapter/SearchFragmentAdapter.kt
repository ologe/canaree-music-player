package dev.olog.presentation.search.adapter

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import dev.olog.core.MediaId
import dev.olog.media.MediaProvider
import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.ComposeListAdapter
import dev.olog.presentation.base.adapter.ComposeViewHolder
import dev.olog.presentation.base.drag.TouchableAdapter
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.search.SearchFragmentViewModel
import dev.olog.presentation.search.ui.ClearRecents
import dev.olog.shared.compose.list.ListItemAlbum
import dev.olog.shared.compose.list.ListItemHeader
import dev.olog.shared.compose.list.ListItemSong

class SearchFragmentAdapter(
    private val mediaProvider: MediaProvider,
    private val navigator: Navigator,
    private val viewModel: SearchFragmentViewModel
) : ComposeListAdapter<SearchItem>(), TouchableAdapter {

    override fun onBindViewHolder(holder: ComposeViewHolder, position: Int) {
        val item = getItem(position)
        holder.setContent {
            when (item) {
                is SearchItem.Recent -> {
                    RecentItem(holder, item)
                }
                is SearchItem.Song -> {
                    ListItemSong(
                        mediaId = item.mediaId,
                        title = item.title,
                        subtitle = item.subtitle,
                        onClick = {
                            mediaProvider.playFromMediaId(item.mediaId, null, null)
                            viewModel.insertToRecent(item.mediaId)
                        },
                        onLongClick = {
                            navigator.toDialog(item.mediaId, holder.itemView)
                        }
                    )
                }
                is SearchItem.ClearRecents -> ClearRecents {
                    viewModel.clearRecentSearches()
                }
                is SearchItem.Header -> ListItemHeader(
                    title = item.title,
                    trailingContent = item.subtitle?.let{ {
                        Text(item.subtitle)
                    } }
                )
                is SearchItem.HorizontalList -> {
                    NestedList(holder, item.items)
                }
            }
        }
    }

    @Composable
    private fun RecentItem(
        holder: ComposeViewHolder,
        item: SearchItem.Recent
    ) {
        ListItemSong(
            mediaId = item.mediaId,
            title = item.title,
            subtitle = item.subtitle,
            trailingContent = {
                IconButton(
                    onClick = { viewModel.deleteFromRecent(item.mediaId) },
                    content = {
                        Icon(
                            imageVector = Icons.Rounded.Clear,
                            contentDescription = null,
                        )
                    }
                )
            },
            onClick = {
                if (item.isPlayable) {
                    mediaProvider.playFromMediaId(item.mediaId, null, null)
                } else {
                    navigator.toDetailFragment(item.mediaId)
                }
            },
            onLongClick = {
                navigator.toDialog(item.mediaId, holder.itemView)
            }
        )
    }

    @Composable
    private fun NestedList(
        holder: ComposeViewHolder,
        items: List<SearchItem.Album>
    ) {
        LazyRow {
            items(items) { nestedItem ->
                ListItemAlbum(
                    mediaId = nestedItem.mediaId,
                    title = nestedItem.title,
                    subtitle = nestedItem.subtitle,
                    modifier = Modifier.width(dimensionResource(R.dimen.item_tab_album_last_player_width)),
                    onClick = {
                        navigator.toDetailFragment(nestedItem.mediaId)
                        viewModel.insertToRecent(nestedItem.mediaId)
                    },
                    onLongClick = {
                        navigator.toDialog(nestedItem.mediaId, holder.itemView)
                    }
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int = when (val item = getItem(position)) {
        is SearchItem.Recent -> if (item.mediaId.isLeaf) R.layout.item_swipeable_compose else 0
        is SearchItem.Song -> R.layout.item_swipeable_compose
        else -> 0
    }

    override fun canInteractWithViewHolder(viewHolder: ViewHolder): Boolean {
        return viewHolder.itemViewType == R.layout.item_swipeable_compose
    }

    override fun onSwipedLeft(viewHolder: ViewHolder) {
        val position = viewHolder.adapterPosition
        val mediaId = when (val item = getItem(position)) {
            is SearchItem.Recent -> item.mediaId
            is SearchItem.Song -> item.mediaId
            is SearchItem.ClearRecents,
            is SearchItem.Header,
            is SearchItem.HorizontalList -> return
        }
        mediaProvider.addToPlayNext(mediaId)
    }

    override fun afterSwipeLeft(viewHolder: ViewHolder) {
        notifyItemChanged(viewHolder.adapterPosition)
    }

}

@Stable
sealed interface SearchItem {

    @Stable
    data class Song(
        val mediaId: MediaId,
        val title: String,
        val subtitle: String,
    ) : SearchItem

    data class Album(
        val mediaId: MediaId,
        val title: String,
        val subtitle: String?,
    )

    @Stable
    data class HorizontalList(
        val items: List<Album>
    ) : SearchItem

    @Stable
    data class Header(
        val title: String,
        val subtitle: String?,
    ) : SearchItem

    @Stable
    data class Recent(
        val mediaId: MediaId,
        val title: String,
        val subtitle: String,
    ) : SearchItem {

        val isPlayable: Boolean
            get() = mediaId.isLeaf

    }

    @Stable
    object ClearRecents : SearchItem

}