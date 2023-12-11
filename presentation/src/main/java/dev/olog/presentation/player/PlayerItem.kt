package dev.olog.presentation.player

import androidx.compose.runtime.Stable
import androidx.recyclerview.widget.DiffUtil
import dev.olog.core.MediaId
import dev.olog.shared.widgets.adapter.SwipeableItem

@Stable
sealed interface PlayerItem {

    @Stable
    object Player : PlayerItem

    @Stable
    data class Track(
        val mediaId: MediaId,
        val title: String,
        val subtitle: String,
        val idInPlaylist: Int,
    ) : PlayerItem, SwipeableItem

    @Stable
    object LoadMore : PlayerItem

    companion object : DiffUtil.ItemCallback<PlayerItem>() {

        override fun areItemsTheSame(
            oldItem: PlayerItem,
            newItem: PlayerItem
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: PlayerItem,
            newItem: PlayerItem
        ): Boolean {
            return oldItem == newItem
        }
    }

}