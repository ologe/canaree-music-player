package dev.olog.presentation.tab.adapter

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import dev.olog.core.MediaId
import dev.olog.media.MediaProvider
import dev.olog.presentation.R
import dev.olog.presentation.base.adapter.ComposeListAdapter
import dev.olog.presentation.base.adapter.ComposeViewHolder
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.tab.TabFragmentViewModel
import dev.olog.shared.TextUtils
import dev.olog.shared.compose.list.ListItemAlbum
import dev.olog.shared.compose.list.ListItemHeader
import dev.olog.shared.compose.list.ListItemPodcast
import dev.olog.shared.compose.list.ListItemShuffle
import dev.olog.shared.compose.list.ListItemSong

internal class TabFragmentAdapter(
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider,
    private val viewModel: TabFragmentViewModel,
) : ComposeListAdapter<TabItem>() {

    override fun bind(holder: ComposeViewHolder, item: TabItem, position: Int) {
        holder.setContent {
            when (item) {
                is TabItem.Album -> {
                    if (item.asRow) {
                        ListItemSong(
                            mediaId = item.mediaId,
                            title = item.title,
                            subtitle = item.subtitle,
                            onClick = {
                                navigator.toDetailFragment(item.mediaId)
                            },
                            onLongClick = {
                                navigator.toDialog(item.mediaId, holder.itemView)
                            }
                        )
                    }
                    ListItemAlbum(
                        mediaId = item.mediaId,
                        title = item.title,
                        subtitle = item.subtitle,
                        onClick = {
                            navigator.toDetailFragment(item.mediaId)
                        },
                        onLongClick = {
                            navigator.toDialog(item.mediaId, holder.itemView)
                        }
                    )
                }
                is TabItem.Header -> ListItemHeader(item.title)
                is TabItem.Podcast -> ListItemPodcast(
                    mediaId = item.mediaId,
                    title = item.title,
                    subtitle = item.subtitle,
                    duration = item.duration,
                    onClick = {
                        mediaProvider.playFromMediaId(item.mediaId, null, null)
                    },
                    onLongClick = {
                        navigator.toDialog(item.mediaId, holder.itemView)
                    }
                )
                is TabItem.Song -> ListItemSong(
                    mediaId = item.mediaId,
                    title = item.title,
                    subtitle = item.subtitle,
                    onClick = {
                        val sort = viewModel.getAllTracksSortOrder()
                        mediaProvider.playFromMediaId(item.mediaId, null, sort)
                    },
                    onLongClick = {
                        navigator.toDialog(item.mediaId, holder.itemView)
                    }
                )

                is TabItem.HorizontalList -> {
                    LazyRow {
                        // TODO clipped
                        items(item.items) { nestedItem ->
                            ListItemAlbum(
                                mediaId = nestedItem.mediaId,
                                title = nestedItem.title,
                                subtitle = nestedItem.subtitle,
                                modifier = Modifier.width(dimensionResource(R.dimen.item_tab_album_last_player_width)),
                                onClick = {
                                    navigator.toDetailFragment(nestedItem.mediaId)
                                },
                                onLongClick = {
                                    navigator.toDialog(nestedItem.mediaId, holder.itemView)
                                }
                            )
                        }
                    }
                }

                TabItem.Shuffle -> ListItemShuffle {
                    mediaProvider.shuffle(MediaId.shuffleId(), null)
                }
            }
        }
    }

}

@Stable
sealed interface TabItem {

    @Stable
    data class Song(
        val mediaId: MediaId,
        val title: String,
        val artist: String,
        val album: String,
    ) : TabItem {
        val subtitle: String
            get() = TextUtils.subtitle(artist, album)
    }

    @Stable
    data class Podcast(
        val mediaId: MediaId,
        val title: String,
        val subtitle: String,
        val duration: String,
    ) : TabItem

    @Stable
    data class Album(
        val mediaId: MediaId,
        val title: String,
        val subtitle: String?,
        val asRow: Boolean,
    ) : TabItem

    @Stable
    object Shuffle: TabItem

    @Stable
    data class Header(val title: String): TabItem

    @Stable
    data class HorizontalList(val items: List<Album>): TabItem

}

fun TabItem.isScrollable() = when (this) {
    is TabItem.Album,
    is TabItem.Podcast,
    is TabItem.Song -> true
    is TabItem.Header,
    is TabItem.HorizontalList,
    TabItem.Shuffle -> false
}