package dev.olog.feature.library.tab.model

import dev.olog.domain.entity.Sort
import dev.olog.domain.mediaid.MediaId
import dev.olog.feature.library.R
import dev.olog.shared.android.DisplayableItemUtils
import kotlin.time.Duration

sealed class TabFragmentModel {

    abstract val layoutType: Int

    interface Scrollable {

        fun sortBy(type: Sort.Type): String

        fun letter(type: Sort.Type): String = sortBy(type).firstOrNull()?.toUpperCase()?.toString() ?: ""

    }

    data class Album(
        override val layoutType: Int,
        val mediaId: MediaId,
        val title: String,
        val subtitle: String?,
    ) : TabFragmentModel(), Scrollable {

        override fun sortBy(type: Sort.Type): String {
            if (mediaId.isAlbum) {
                return when (type) {
                    Sort.Type.TITLE -> title
                    else -> subtitle.orEmpty() // artist
                }
            }
            return title
        }
    }

    data class Track(
        val mediaId: MediaId,
        val title: String,
        val artist: String,
        val album: String,
    ) : TabFragmentModel(), Scrollable {

        override val layoutType: Int = R.layout.item_tab_track

        val subtitle: String
            get() = DisplayableItemUtils.trackSubtitle(artist, album)

        override fun sortBy(type: Sort.Type): String = when (type) {
            Sort.Type.TITLE -> title
            Sort.Type.ALBUM -> album
            Sort.Type.ARTIST -> artist
            else -> title
        }

    }

    data class Podcast(
        val mediaId: MediaId,
        val title: String,
        val artist: String,
        val album: String,
        val duration: Duration,
    ) : TabFragmentModel(), Scrollable {

        override val layoutType: Int = R.layout.item_tab_podcast

        val subtitle: String
            get() = DisplayableItemUtils.trackSubtitle(artist, album)

        override fun sortBy(type: Sort.Type): String = when (type) {
            Sort.Type.TITLE -> title
            Sort.Type.ALBUM -> album
            Sort.Type.ARTIST -> artist
            else -> title
        }

        val formattedDuration: String
            get() = "${duration.inSeconds.toInt()}m"

    }

    data class Header(
        val title: String
    ) : TabFragmentModel() {
        override val layoutType: Int = R.layout.item_tab_header
    }

    object Shuffle : TabFragmentModel() {
        override val layoutType: Int = R.layout.item_tab_shuffle
    }

    object RecentlyPlayedAlbumsList : TabFragmentModel() {
        override val layoutType: Int = R.layout.item_tab_last_played_album_list
    }
    object RecentlyPlayedArtistList : TabFragmentModel() {
        override val layoutType: Int = R.layout.item_tab_last_played_artist_list
    }

    object RecentlyAddedAlbumsList : TabFragmentModel() {
        override val layoutType: Int = R.layout.item_tab_recently_added_album_list
    }
    object RecentlyAddedArtistList : TabFragmentModel() {
        override val layoutType: Int = R.layout.item_tab_recently_added_album_list
    }

}