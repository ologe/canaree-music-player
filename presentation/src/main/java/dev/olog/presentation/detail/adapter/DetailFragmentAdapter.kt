package dev.olog.presentation.detail.adapter

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.recyclerview.widget.RecyclerView
import dev.olog.core.MediaId
import dev.olog.core.entity.AutoPlaylist
import dev.olog.media.MediaProvider
import dev.olog.presentation.R
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.presentation.base.drag.TouchableAdapter
import dev.olog.presentation.detail.DetailFragmentViewModel
import dev.olog.presentation.detail.DetailSortDialog
import dev.olog.presentation.detail.widget.DetailHeader
import dev.olog.presentation.detail.widget.DetailListItemAlbumItem
import dev.olog.presentation.detail.widget.DetailListItemFolderItem
import dev.olog.presentation.detail.widget.DetailListItemPlaylistItem
import dev.olog.presentation.detail.widget.DetailSongsSort
import dev.olog.presentation.navigator.Navigator
import dev.olog.shared.compose.component.ComposeListAdapter
import dev.olog.shared.compose.component.ComposeViewHolder
import dev.olog.shared.compose.component.IconButton
import dev.olog.shared.compose.listitem.ListItemFooter
import dev.olog.shared.compose.listitem.ListItemHeader
import dev.olog.shared.compose.listitem.ListItemShuffle
import dev.olog.shared.compose.listitem.ListItemTrack

internal class DetailFragmentAdapter(
    private val mediaId: MediaId,
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider,
    private val viewModel: DetailFragmentViewModel,
    private val dragListener: IDragListener
) : ComposeListAdapter<DetailFragmentItem>(DetailFragmentItem),
    TouchableAdapter {

    @Composable
    override fun Content(viewHolder: ComposeViewHolder, item: DetailFragmentItem) {
        when (item) {
            is DetailFragmentItem.Header -> {
                DetailHeader(
                    mediaId = item.mediaId,
                    title = item.title,
                    subtitle = item.subtitle,
                    biography = item.biography,
                )
            }
            is DetailFragmentItem.Siblings -> {
                DetailSiblingsContent(
                    siblings = item,
                    onClick = navigator::toDetailFragment,
                    onLongClick = {
                        navigator.toDialog(it, viewHolder.itemView)
                    }
                )
            }
            is DetailFragmentItem.RecentlyAdded -> {
                DetailRecentlyAddedContent(
                    recentlyAdded = item,
                    onClick = mediaProvider::playRecentlyAdded,
                    onLongClick = {
                        navigator.toDialog(it, viewHolder.itemView)
                    },
                    onSeeAllClick = {
                        navigator.toRecentlyAdded(mediaId)
                    }
                )
            }
            is DetailFragmentItem.MostPlayed -> {
                DetailMostPlayedContent(
                    mostPlayed = item,
                    onClick = mediaProvider::playMostPlayed,
                    onLongClick = {
                        navigator.toDialog(it, viewHolder.itemView)
                    }
                )
            }
            is DetailFragmentItem.RelatedArtists -> {
                DetailRelatedArtistsContent(
                    relatedArtists = item,
                    onClick = navigator::toDetailFragment,
                    onLongClick = {
                        navigator.toDialog(it, viewHolder.itemView)
                    },
                    onSeeAllClick = { navigator.toRelatedArtists(mediaId) }
                )
            }
            is DetailFragmentItem.SongsHeader -> {
                ListItemHeader(stringResource(R.string.common_all_tracks)) {
                    // TODO tap tutorial target
//                if (viewModel.showSortByTutorialIfNeverShown()) {
//                    TutorialTapTarget.sortBy(sortText, sortImage)
//                }
                    DetailSongsSort(
                        sort = item.sort,
                        onTypeClick = {
                            DetailSortDialog().show(viewHolder.itemView, mediaId, item.sort.type) { newSortType ->
                                viewModel.updateSortOrder(newSortType)
                            }
                        },
                        onArrangingClick = viewModel::toggleSortArranging,
                    )
                }
            }
            is DetailFragmentItem.Shuffle -> {
                ListItemShuffle {
                    mediaProvider.shuffle(mediaId, viewModel.getFilter())
                }
            }
            is DetailFragmentItem.DurationFooter -> {
                ListItemFooter(item.text)
            }
            is DetailFragmentItem.Track.Default -> {
                ListItemTrack(
                    mediaId = item.mediaId,
                    title = item.title,
                    subtitle = item.subtitle,
                    onClick = {
                        mediaProvider.playFromMediaId(item.mediaId, viewModel.getFilter(), viewModel.getSort())
                    },
                    onLongClick = {
                        navigator.toDialog(item.mediaId, viewHolder.itemView)
                    },
                    trailingContent = {
                        IconButton(
                            drawableRes = R.drawable.vd_more,
                            onClick = {
                                navigator.toDialog(item.mediaId, viewHolder.itemView)
                            }
                        )
                    }
                )
            }
            is DetailFragmentItem.Track.ForAlbum -> {
                DetailListItemAlbumItem(
                    trackNumber = item.trackNumber,
                    title = item.title,
                    subtitle = item.subtitle,
                    onClick = {
                        mediaProvider.playFromMediaId(item.mediaId, viewModel.getFilter(), viewModel.getSort())
                    },
                    onLongClick = {
                        navigator.toDialog(item.mediaId, viewHolder.itemView)
                    }
                )
            }
            is DetailFragmentItem.Track.ForFolder -> {
                DetailListItemFolderItem(
                    mediaId = item.mediaId,
                    title = item.title,
                    subtitle = item.subtitle,
                    trackNumber = item.trackNumber,
                    onClick = {
                        mediaProvider.playFromMediaId(item.mediaId, viewModel.getFilter(), viewModel.getSort())
                    },
                    onLongClick = {
                        navigator.toDialog(item.mediaId, viewHolder.itemView)
                    }
                )
            }
            is DetailFragmentItem.Track.ForPlaylist -> {
                DetailListItemPlaylistItem(
                    mediaId = item.mediaId,
                    title = item.title,
                    subtitle = item.subtitle,
                    onClick = {
                        mediaProvider.playFromMediaId(item.mediaId, viewModel.getFilter(), viewModel.getSort())
                    },
                    onLongClick = {
                        navigator.toDialog(item.mediaId, viewHolder.itemView)
                    },
                    onStartDrag = {
                        dragListener.onStartDrag(viewHolder)
                    }
                )
            }
        }
    }

    val canSwipeRight: Boolean
        get() {
            if (mediaId.isPlaylist || mediaId.isPodcastPlaylist) {
                val playlistId = mediaId.resolveId
                return playlistId != AutoPlaylist.LAST_ADDED.id || !AutoPlaylist.isAutoPlaylist(
                    playlistId
                )
            }
            return false
        }

    override fun onClearView() {
        viewModel.processMove()
    }

    override fun onMoved(from: Int, to: Int) {
        val diff = currentList.indexOfFirst { it is DetailFragmentItem.Track }
        move(from, to)
        notifyItemMoved(from, to)
        viewModel.addMove(from - diff, to - diff)
    }

    override fun onSwipedRight(viewHolder: RecyclerView.ViewHolder) {
        val position = viewHolder.bindingAdapterPosition
        val item = getItem(position)
        if (item is DetailFragmentItem.Track.ForPlaylist) {
            removeAt(position)
            notifyItemRemoved(position)
            viewModel.removeFromPlaylist(item)
        }
    }

    override fun afterSwipeRight(viewHolder: RecyclerView.ViewHolder) {

    }

    override fun onSwipedLeft(viewHolder: RecyclerView.ViewHolder) {
        val item = getItem(viewHolder.bindingAdapterPosition)
        if (item is DetailFragmentItem.Track) {
            mediaProvider.addToPlayNext(item.mediaId)
        }
    }

    override fun afterSwipeLeft(viewHolder: RecyclerView.ViewHolder) {
        notifyItemChanged(viewHolder.bindingAdapterPosition)
    }

}