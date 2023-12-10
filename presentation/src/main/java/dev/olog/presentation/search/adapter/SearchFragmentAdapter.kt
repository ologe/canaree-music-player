package dev.olog.presentation.search.adapter

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.recyclerview.widget.RecyclerView
import dev.olog.media.MediaProvider
import dev.olog.presentation.R
import dev.olog.presentation.base.drag.TouchableAdapter
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.search.SearchFragmentViewModel
import dev.olog.shared.compose.component.ComposeListAdapter
import dev.olog.shared.compose.component.ComposeViewHolder
import dev.olog.shared.compose.component.IconButton
import dev.olog.shared.compose.component.Text
import dev.olog.shared.compose.listitem.ListItemAlbum
import dev.olog.shared.compose.listitem.ListItemFooter
import dev.olog.shared.compose.listitem.ListItemHeader
import dev.olog.shared.compose.listitem.ListItemTrack

class SearchFragmentAdapter(
    private val mediaProvider: MediaProvider,
    private val navigator: Navigator,
    private val viewModel: SearchFragmentViewModel
) : ComposeListAdapter<SearchFragmentItem>(SearchFragmentItem), TouchableAdapter {

    @Composable
    override fun Content(viewHolder: ComposeViewHolder, item: SearchFragmentItem) {
        when (item) {
            is SearchFragmentItem.Header -> {
                ListItemHeader(
                    title = item.title,
                    trailingContent = {
                        if (item.subtitle != null) {
                            Text(item.subtitle)
                        }
                    }
                )
            }
            is SearchFragmentItem.Album -> {
                ListItemAlbum(
                    mediaId = item.mediaId,
                    title = item.title,
                    subtitle = item.subtitle,
                    onClick = {
                        navigator.toDetailFragment(item.mediaId)
                        viewModel.insertToRecent(item.mediaId)
                    },
                    onLongClick = {
                        navigator.toDialog(item.mediaId, viewHolder.itemView)
                    }
                )
            }
            is SearchFragmentItem.Track -> {
                ListItemTrack(
                    mediaId = item.mediaId,
                    title = item.title,
                    subtitle = item.subtitle,
                    onClick = {
                        mediaProvider.playFromMediaId(item.mediaId, null, null)
                        viewModel.insertToRecent(item.mediaId)
                    },
                    onLongClick = {
                        navigator.toDialog(item.mediaId, viewHolder.itemView)
                    },
                    trailingContent = {
                        IconButton(R.drawable.vd_more) {
                            navigator.toDialog(item.mediaId, viewHolder.itemView)
                        }
                    }
                )
            }
            is SearchFragmentItem.List -> {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                ) { // TODO pager snap on scroll?
                    items(item.items) { nestedItem ->
                        Box(Modifier.width(dimensionResource(R.dimen.item_tab_album_last_player_width))) {
                            Content(viewHolder, nestedItem)
                        }
                    }
                }
            }
            is SearchFragmentItem.Recent -> {
                ListItemTrack(
                    mediaId = item.mediaId,
                    title = item.title,
                    subtitle = item.subtitle,
                    onClick = {
                        if (item.isPlayable) {
                            mediaProvider.playFromMediaId(item.mediaId, null, null)
                        } else {
                            navigator.toDetailFragment(item.mediaId)
                        }
                    },
                    onLongClick = {
                        navigator.toDialog(item.mediaId, viewHolder.itemView)
                    },
                    trailingContent = {
                        IconButton(R.drawable.vd_clear) {
                            viewModel.deleteFromRecent(item.mediaId)
                        }
                    }
                )
            }
            is SearchFragmentItem.ClearRecents -> {
                ListItemFooter(
                    text = stringResource(R.string.search_clear_recent_searches),
                ) {
                    viewModel.clearRecentSearches()
                }
            }
        }
    }

    override fun onSwipedLeft(viewHolder: RecyclerView.ViewHolder) {
        val position = viewHolder.bindingAdapterPosition
        val item = getItem(position)
        when (item) {
            is SearchFragmentItem.Album -> mediaProvider.addToPlayNext(item.mediaId)
            is SearchFragmentItem.Recent -> mediaProvider.addToPlayNext(item.mediaId)
            is SearchFragmentItem.Track -> mediaProvider.addToPlayNext(item.mediaId)
            is SearchFragmentItem.ClearRecents,
            is SearchFragmentItem.Header,
            is SearchFragmentItem.List -> return
        }
    }

    override fun afterSwipeLeft(viewHolder: RecyclerView.ViewHolder) {
        notifyItemChanged(viewHolder.bindingAdapterPosition)
    }

}