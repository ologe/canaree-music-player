package dev.olog.feature.search.model

import dev.olog.domain.mediaid.MediaId
import dev.olog.feature.search.R

sealed class SearchFragmentModel {

    abstract val layoutType: Int

    data class Track(
        val mediaId: MediaId.Track,
        val title: String,
        val subtitle: String?
    ) : SearchFragmentModel() {
        override val layoutType: Int = R.layout.item_search_song
    }

    data class Album(
        override val layoutType: Int,
        val mediaId: MediaId.Category,
        val title: String,
        val subtitle: String?,
    ) : SearchFragmentModel()

    data class Header(
        val title: String,
        val subtitle: String,
    ) : SearchFragmentModel() {
        override val layoutType: Int = R.layout.item_search_header
    }

    // recent

    data class RecentTrack(
        val mediaId: MediaId.Track,
        val title: String,
        val subtitle: String?
    ) : SearchFragmentModel() {
        override val layoutType: Int = R.layout.item_search_recent
    }

    data class RecentAlbum(
        override val layoutType: Int,
        val mediaId: MediaId.Category,
        val title: String,
        val subtitle: String
    ) : SearchFragmentModel()

    object RecentHeader : SearchFragmentModel() {
        override val layoutType: Int = R.layout.item_search_recent_header
    }

    object ClearRecent: SearchFragmentModel() {
        override val layoutType: Int = R.layout.item_search_clear_recent
    }

    // list

    object FoldersList : SearchFragmentModel() {
        override val layoutType: Int = R.layout.item_search_list_folders
    }
    object AlbumsList : SearchFragmentModel() {
        override val layoutType: Int = R.layout.item_search_list_albums
    }
    object ArtistsList : SearchFragmentModel() {
        override val layoutType: Int = R.layout.item_search_list_artists
    }
    object PlaylistList : SearchFragmentModel() {
        override val layoutType: Int = R.layout.item_search_list_playlists
    }
    object GenreList : SearchFragmentModel() {
        override val layoutType: Int = R.layout.item_search_list_genres
    }

}