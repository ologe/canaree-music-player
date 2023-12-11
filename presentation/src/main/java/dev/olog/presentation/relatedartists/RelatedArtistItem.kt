package dev.olog.presentation.relatedartists

import androidx.compose.runtime.Stable
import androidx.recyclerview.widget.DiffUtil
import dev.olog.core.MediaId

@Stable
data class RelatedArtistItem(
    val mediaId: MediaId,
    val title: String,
    val subtitle: String,
) {

    companion object : DiffUtil.ItemCallback<RelatedArtistItem>() {
        override fun areItemsTheSame(
            oldItem: RelatedArtistItem,
            newItem: RelatedArtistItem
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: RelatedArtistItem,
            newItem: RelatedArtistItem
        ): Boolean {
            return oldItem == newItem
        }
    }

}