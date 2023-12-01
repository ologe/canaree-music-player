package dev.olog.presentation.tab.adapter

import android.view.View
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

internal class TabFragmentAdapter(
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider,
    private val viewModel: TabFragmentViewModel,
) : ComposeListAdapter<TabFragmentItem>(TabFragmentItem) {

    @Composable
    override fun Content(view: View, item: TabFragmentItem) {
        when (item) {
            is TabFragmentItem.Track -> ListItemTrack(
                mediaId = item.mediaId,
                title = item.title,
                subtitle = item.subtitle,
                paddingValues = LocalScreenSpacing.current,
                onClick = {
                    val sort = viewModel.getAllTracksSortOrder(item.mediaId)
                    mediaProvider.playFromMediaId(item.mediaId, null, sort)
                },
                onLongClick = {
                    navigator.toDialog(item.mediaId, view)
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
                        navigator.toDialog(item.mediaId, view)
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
                            navigator.toDialog(item.mediaId, view)
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
                            navigator.toDialog(item.mediaId, view)
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
                LazyRow(Modifier.fillMaxWidth()) {
                    items(item.items) { item ->

                    }
                }
            }
            is TabFragmentItem.Shuffle -> {
                ListItemShuffle(
                    paddingValues = LocalScreenSpacing.current,
                    onClick = {
                        mediaProvider.shuffle(MediaId.shuffleId(), null)
                    }
                )
            }
        }
    }

}
