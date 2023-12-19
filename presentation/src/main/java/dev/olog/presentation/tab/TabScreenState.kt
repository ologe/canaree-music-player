package dev.olog.presentation.tab

import androidx.compose.runtime.Stable
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.sort.SortType
import dev.olog.presentation.model.DisplayableTrack
import dev.olog.presentation.widgets.fascroller.ScrollableItem
import kotlin.time.Duration

@Stable
data class TabScreenState(
    val items: List<TabListItem>,
    val letters: List<String>,
    val spanCount: Int,
)

@Stable
sealed interface TabListItem {

    @Stable
    data class Track(
        val mediaId: MediaId,
        val title: String,
        val artist: String,
        val album: String,
    ) : TabListItem, ScrollableItem {
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
    ) : TabListItem, ScrollableItem {
        val subtitle: String = DisplayableTrack.subtitle(artist, album)
        override fun getText(order: SortType): String = title
    }

    @Stable
    sealed interface Album : TabListItem {
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
    data object Shuffle : TabListItem

    @Stable
    data class List(val items: kotlin.collections.List<Album>) : TabListItem

    @Stable
    data class Header(val text: String) : TabListItem

}