package dev.olog.presentation.queue

import androidx.compose.material3.Text
import androidx.compose.runtime.Stable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import dev.olog.core.MediaId
import dev.olog.media.MediaProvider
import dev.olog.presentation.base.adapter.ComposeListAdapter
import dev.olog.presentation.base.adapter.ComposeViewHolder
import dev.olog.presentation.base.adapter.InteractableItem
import dev.olog.presentation.base.drag.IDragListener
import dev.olog.presentation.base.drag.TouchableAdapter
import dev.olog.presentation.navigator.Navigator
import dev.olog.shared.compose.component.DragHandle
import dev.olog.shared.compose.list.ListItemSong

class PlayingQueueFragmentAdapter(
    private val mediaProvider: MediaProvider,
    private val navigator: Navigator,
    private val dragListener: IDragListener,
    private val viewModel: PlayingQueueFragmentViewModel
) : ComposeListAdapter<QueueItem>(DiffCallbackQueueItem), TouchableAdapter {

    override fun bind(holder: ComposeViewHolder, item: QueueItem, position: Int) {
        holder.setContent {
            ListItemSong(
                mediaId = item.mediaId,
                title = item.title,
                subtitle = item.subtitle,
                indexContent = { Text(text = item.relativePosition) },
                onClick = { mediaProvider.skipToQueueItem(item.idInPlaylist) },
                onLongClick = { navigator.toDialog(item.mediaId, holder.itemView) },
                trailingContent = { DragHandle { dragListener.onStartDrag(holder) } }
            )
        }
    }

    override fun onMoved(from: Int, to: Int) {
        mediaProvider.swap(from, to)
        moveItem(from, to)
    }

    override fun onSwipedRight(viewHolder: ViewHolder) {
        mediaProvider.remove(viewHolder.adapterPosition)
    }

    override fun afterSwipeRight(viewHolder: ViewHolder) {
        removeItem(viewHolder.adapterPosition)
        commitChange {
            viewModel.updateQueue(it)
        }
    }

    override fun onClearView() {
        commitChange {
            viewModel.updateQueue(it)
        }
    }

}

private object DiffCallbackQueueItem : DiffUtil.ItemCallback<QueueItem>() {
    override fun areItemsTheSame(
        oldItem: QueueItem,
        newItem: QueueItem
    ): Boolean {
        return oldItem.mediaId == newItem.mediaId
    }

    override fun areContentsTheSame(
        oldItem: QueueItem,
        newItem: QueueItem
    ): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(
        oldItem: QueueItem,
        newItem: QueueItem
    ): Any {
        return newItem
    }
}

@Stable
data class QueueItem(
    val id: Long,
    val mediaId: MediaId,
    val title: String,
    val subtitle: String,
    val idInPlaylist: Int,
    val isPlaying: Boolean,
    val relativePosition: String,
) : InteractableItem