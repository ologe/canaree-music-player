package dev.olog.presentation.tab.adapter

import androidx.compose.runtime.Stable
import androidx.recyclerview.widget.DiffUtil
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.sort.SortType
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.presentation.widgets.fascroller.ScrollableItem
import kotlin.time.Duration

@Stable
sealed interface TabFragmentItem {

    @Stable
    data class Track(
        val mediaId: MediaId,
        val title: String,
        val artist: String,
        val album: String,
    ) : TabFragmentItem, ScrollableItem {
        val subtitle: String = DisplayableTrack.subtitle(artist, album)
        override fun getText(order: SortType): String = when (order) {
            SortType.TITLE -> title
            SortType.ARTIST -> artist
            SortType.ALBUM -> album
            else -> title
        }
    }

    @Stable
    data class Podcast(
        val mediaId: MediaId,
        val title: String,
        val artist: String,
        val album: String,
        val duration: Duration,
    ) : TabFragmentItem, ScrollableItem {
        val subtitle: String = DisplayableTrack.subtitle(artist, album)
        override fun getText(order: SortType): String = title
    }

    @Stable
    sealed interface Album : TabFragmentItem {
        val mediaId: MediaId
        val title: String
        val subtitle: String?
        val asRow: Boolean

        @Stable
        data class Scrollable(
            override val mediaId: MediaId,
            override val title: String,
            override val subtitle: String,
            override val asRow: Boolean,
        ) : Album, ScrollableItem {
            override fun getText(order: SortType): String {
                if (mediaId.category == MediaIdCategory.ALBUMS) {
                    return when (order) {
                        SortType.TITLE -> title
                        SortType.ARTIST -> subtitle
                        else -> title
                    }
                }
                return title
            }
        }

        @Stable
        data class NonScrollable(
            override val mediaId: MediaId,
            override val title: String,
            override val subtitle: String?,
        ) : Album {
            override val asRow: Boolean = false
        }

    }

    @Stable
    object Shuffle : TabFragmentItem

    @Stable
    data class List(val items: kotlin.collections.List<TabFragmentItem>) : TabFragmentItem

    @Stable
    data class Header(val text: String) : TabFragmentItem

    companion object : DiffUtil.ItemCallback<TabFragmentItem>() {

        override fun areItemsTheSame(oldItem: TabFragmentItem, newItem: TabFragmentItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: TabFragmentItem,
            newItem: TabFragmentItem
        ): Boolean {
            return oldItem == newItem
        }
    }

}