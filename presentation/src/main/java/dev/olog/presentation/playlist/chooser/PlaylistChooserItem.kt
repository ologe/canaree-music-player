package dev.olog.presentation.playlist.chooser

import androidx.compose.runtime.Stable
import androidx.recyclerview.widget.DiffUtil
import dev.olog.core.MediaId

@Stable
data class PlaylistChooserItem(
    val mediaId: MediaId,
    val title: String,
    val subtitle: String,
) {

    companion object : DiffUtil.ItemCallback<PlaylistChooserItem>() {
        override fun areItemsTheSame(
            oldItem: PlaylistChooserItem,
            newItem: PlaylistChooserItem
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: PlaylistChooserItem,
            newItem: PlaylistChooserItem
        ): Boolean {
            return oldItem == newItem
        }
    }

}