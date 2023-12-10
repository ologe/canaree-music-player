package dev.olog.presentation.detail.adapter

import androidx.compose.runtime.Stable
import androidx.recyclerview.widget.DiffUtil
import dev.olog.core.MediaId
import dev.olog.core.entity.sort.SortEntity
import dev.olog.shared.widgets.adapter.SwipeableItem

@Stable
sealed interface DetailFragmentItem {

    @Stable
    data class Header(
        val mediaId: MediaId,
        val title: String,
        val subtitle: String,
        val biography: String?,
    ) : DetailFragmentItem

    @Stable
    sealed interface Track : DetailFragmentItem {

        val mediaId: MediaId

        @Stable
        data class Default(
            override val mediaId: MediaId,
            val title: String,
            val subtitle: String,
        ) : Track

        @Stable
        data class ForAlbum(
            override val mediaId: MediaId,
            val title: String,
            val subtitle: String,
            val trackNumber: String,
        ) : Track

        @Stable
        data class ForPlaylist(
            override val mediaId: MediaId,
            val title: String,
            val subtitle: String,
            val idInPlaylist: Int,
        ) : Track, SwipeableItem
        @Stable
        data class ForFolder(
            override val mediaId: MediaId,
            val title: String,
            val subtitle: String,
            val trackNumber: String,
        ) : Track
    }

    @Stable
    data class Siblings(
        val header: String, // TODO
        val items: List<DetailSiblingItem>
    ) : DetailFragmentItem

    @Stable
    data class MostPlayed(
        val items: List<DetailMostPlayedItem>
    ) : DetailFragmentItem

    @Stable
    data class RecentlyAdded(
        val items: List<DetailRecentlyAddedItem>
    ) : DetailFragmentItem

    @Stable
    data class RelatedArtists(
        val items: List<DetailRelatedArtistItem>
    ) : DetailFragmentItem

    @Stable
    data class SongsHeader(
        val sort: SortEntity,
    ) : DetailFragmentItem

    @Stable
    object Shuffle : DetailFragmentItem

    @Stable
    data class DurationFooter(
        val text: String,
    ) : DetailFragmentItem

    companion object : DiffUtil.ItemCallback<DetailFragmentItem>() {
        override fun areItemsTheSame(
            oldItem: DetailFragmentItem,
            newItem: DetailFragmentItem
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: DetailFragmentItem,
            newItem: DetailFragmentItem
        ): Boolean {
            return oldItem == newItem
        }
    }

}

@Stable
data class DetailSiblingItem(
    val mediaId: MediaId,
    val title: String,
    val subtitle: String,
)

@Stable
data class DetailMostPlayedItem(
    val mediaId: MediaId,
    val title: String,
    val subtitle: String,
    val position: String,
)

@Stable
data class DetailRecentlyAddedItem(
    val mediaId: MediaId,
    val title: String,
    val subtitle: String,
)

@Stable
data class DetailRelatedArtistItem(
    val mediaId: MediaId,
    val title: String,
    val subtitle: String,
)