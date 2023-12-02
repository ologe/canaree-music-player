package dev.olog.presentation.createplaylist.mapper

import androidx.compose.runtime.Stable
import androidx.recyclerview.widget.DiffUtil
import dev.olog.core.MediaId
import dev.olog.core.entity.sort.SortType
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.presentation.widgets.fascroller.ScrollableItem

@Stable
data class CreatePlaylistFragmentItem(
    val mediaId: MediaId,
    val title: String,
    val artist: String,
    val album: String,
    val isChecked: Boolean,
) : ScrollableItem {

    val subtitle: String = DisplayableTrack.subtitle(artist, album)

    override fun getText(order: SortType): String {
        return title
    }

    companion object : DiffUtil.ItemCallback<CreatePlaylistFragmentItem>() {
        override fun areItemsTheSame(
            oldItem: CreatePlaylistFragmentItem,
            newItem: CreatePlaylistFragmentItem
        ): Boolean {
            return oldItem.mediaId == newItem.mediaId
        }

        override fun areContentsTheSame(
            oldItem: CreatePlaylistFragmentItem,
            newItem: CreatePlaylistFragmentItem
        ): Boolean {
            return oldItem == newItem
        }
    }

}