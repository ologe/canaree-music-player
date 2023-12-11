package dev.olog.presentation.recentlyadded

import androidx.compose.runtime.Stable
import androidx.recyclerview.widget.DiffUtil
import dev.olog.core.MediaId
import dev.olog.shared.widgets.adapter.SwipeableItem

@Stable
data class RecentlyAddedItem(
    val mediaId: MediaId,
    val title: String,
    val subtitle: String,
) : SwipeableItem {

    companion object : DiffUtil.ItemCallback<RecentlyAddedItem>() {
        override fun areItemsTheSame(
            oldItem: RecentlyAddedItem,
            newItem: RecentlyAddedItem
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: RecentlyAddedItem,
            newItem: RecentlyAddedItem
        ): Boolean {
            return oldItem == newItem
        }
    }

}