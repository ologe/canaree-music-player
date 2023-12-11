package dev.olog.presentation.recentlyadded

import androidx.compose.runtime.Composable
import androidx.recyclerview.widget.RecyclerView
import dev.olog.media.MediaProvider
import dev.olog.presentation.R
import dev.olog.presentation.base.drag.TouchableAdapter
import dev.olog.presentation.navigator.Navigator
import dev.olog.shared.compose.component.ComposeListAdapter
import dev.olog.shared.compose.component.ComposeViewHolder
import dev.olog.shared.compose.component.IconButton
import dev.olog.shared.compose.listitem.ListItemTrack

class RecentlyAddedFragmentAdapter(
    private val navigator: Navigator,
    private val mediaProvider: MediaProvider,
) : ComposeListAdapter<RecentlyAddedItem>(RecentlyAddedItem), TouchableAdapter {

    @Composable
    override fun Content(viewHolder: ComposeViewHolder, item: RecentlyAddedItem) {
        ListItemTrack(
            mediaId = item.mediaId,
            title = item.title,
            subtitle = item.subtitle,
            onClick = {
                mediaProvider.playFromMediaId(item.mediaId, null, null)
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

    override fun afterSwipeLeft(viewHolder: RecyclerView.ViewHolder) {
        val item = getItem(viewHolder.bindingAdapterPosition)
        mediaProvider.addToPlayNext(item.mediaId)
    }

}