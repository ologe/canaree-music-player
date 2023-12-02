package dev.olog.presentation.search.adapter

import androidx.compose.runtime.Stable
import androidx.recyclerview.widget.DiffUtil
import dev.olog.core.MediaId
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.shared.widgets.adapter.SwipeableItem

@Stable
sealed interface SearchFragmentItem {

    @Stable
    data class Track(
        val mediaId: MediaId,
        val title: String,
        val artist: String,
        val album: String,
    ) : SearchFragmentItem, SwipeableItem {
        val subtitle: String = DisplayableTrack.subtitle(artist, album)
    }

    @Stable
    data class Album(
        val mediaId: MediaId,
        val title: String,
        val subtitle: String?,
    ) : SearchFragmentItem

    @Stable
    data class Recent(
        val mediaId: MediaId,
        val title: String,
        val subtitle: String,
        val isPlayable: Boolean,
    ) : SearchFragmentItem, SwipeableItem

    @Stable
    object ClearRecents : SearchFragmentItem

    @Stable
    data class Header(
        val title: String,
        val subtitle: String?,
    ) : SearchFragmentItem

    @Stable
    data class List(val items: kotlin.collections.List<Album>): SearchFragmentItem

    companion object : DiffUtil.ItemCallback<SearchFragmentItem>() {
        override fun areItemsTheSame(
            oldItem: SearchFragmentItem,
            newItem: SearchFragmentItem
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: SearchFragmentItem,
            newItem: SearchFragmentItem
        ): Boolean {
            return oldItem == newItem
        }
    }

}