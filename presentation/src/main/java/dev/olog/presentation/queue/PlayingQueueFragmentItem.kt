package dev.olog.presentation.queue

import androidx.compose.runtime.Stable
import androidx.recyclerview.widget.DiffUtil
import dev.olog.core.MediaId
import dev.olog.shared.widgets.adapter.SwipeableItem

@Stable
data class PlayingQueueFragmentItem(
    val mediaId: MediaId,
    val title: String,
    val subtitle: String,
    val idInPlaylist: Int,
    val relativePosition: String,
    val isCurrentlyPlaying: Boolean,
) : SwipeableItem {

    companion object : DiffUtil.ItemCallback<PlayingQueueFragmentItem>() {

        override fun areItemsTheSame(
            oldItem: PlayingQueueFragmentItem,
            newItem: PlayingQueueFragmentItem
        ): Boolean {
            return oldItem.mediaId == newItem.mediaId
        }

        override fun areContentsTheSame(
            oldItem: PlayingQueueFragmentItem,
            newItem: PlayingQueueFragmentItem
        ): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(
            oldItem: PlayingQueueFragmentItem,
            newItem: PlayingQueueFragmentItem
        ): Any {
            return newItem
        }
    }

}