package dev.olog.presentation.tab.adapter

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import dev.olog.core.MediaId
import dev.olog.media.MediaProvider
import dev.olog.presentation.navigator.Navigator
import dev.olog.presentation.tab.TabFragmentViewModel
import dev.olog.shared.compose.component.ComposeListAdapter
import dev.olog.shared.compose.listitem.ListItemAlbum
import dev.olog.shared.compose.listitem.ListItemHeader
import dev.olog.shared.compose.listitem.ListItemPodcast
import dev.olog.shared.compose.listitem.ListItemShuffle
import dev.olog.shared.compose.listitem.ListItemTrack
import dev.olog.shared.compose.theme.LocalScreenSpacing
import dev.olog.presentation.R
import dev.olog.shared.compose.component.ComposeViewHolder

internal class TabFragmentAdapter(
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider,
    private val viewModel: TabFragmentViewModel,
) : ComposeListAdapter<TabFragmentItem>(TabFragmentItem) {

    @Composable
    override fun Content(viewHolder: ComposeViewHolder, item: TabFragmentItem) {
        when (item) {
            is TabFragmentItem.Track -> ListItemTrack(
                mediaId = item.mediaId,
                title = item.title,
                subtitle = item.subtitle,
                contentPadding = LocalScreenSpacing.current,
                onClick = {
                    val sort = viewModel.getAllTracksSortOrder(item.mediaId)
                    mediaProvider.playFromMediaId(item.mediaId, null, sort)
                },
                onLongClick = {
                    navigator.toDialog(item.mediaId, viewHolder.itemView)
                }
            )
            is TabFragmentItem.Podcast -> {
                ListItemPodcast(
                    mediaId = item.mediaId,
                    title = item.title,
                    subtitle = item.subtitle,
                    duration = "${item.duration.inWholeMinutes}m",
                    onClick = {
                        val sort = viewModel.getAllTracksSortOrder(item.mediaId)
                        mediaProvider.playFromMediaId(item.mediaId, null, sort)
                    },
                    onLongClick = {
                        navigator.toDialog(item.mediaId, viewHolder.itemView)
                    }
                )
            }
            is TabFragmentItem.Album -> {
                if (item.asRow) {
                    ListItemTrack(
                        mediaId = item.mediaId,
                        title = item.title,
                        subtitle = item.subtitle.orEmpty(),
                        onClick = {
                            navigator.toDetailFragment(item.mediaId)
                        },
                        onLongClick = {
                            navigator.toDialog(item.mediaId, viewHolder.itemView)
                        }
                    )
                } else {
                    ListItemAlbum(
                        mediaId = item.mediaId,
                        title = item.title,
                        subtitle = item.subtitle,
                        onClick = {
                            navigator.toDetailFragment(item.mediaId)
                        },
                        onLongClick = {
                            navigator.toDialog(item.mediaId, viewHolder.itemView)
                        }
                    )
                }
            }
            is TabFragmentItem.Header -> {
                ListItemHeader(
                    text = item.text,
                )
            }
            is TabFragmentItem.List -> {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    // TODO content padding
                ) {
                    items(item.items) { nestedItem ->
                        // TODO check how it looks
                        Box(Modifier.width(dimensionResource(id = R.dimen.item_tab_album_last_player_width))) {
                            Content(viewHolder, nestedItem)
                        }
                    }
                }
            }
            is TabFragmentItem.Shuffle -> {
                ListItemShuffle(
                    contentPadding = LocalScreenSpacing.current,
                    onClick = {
                        mediaProvider.shuffle(MediaId.shuffleId(), null)
                    }
                )
            }
        }
    }

}
