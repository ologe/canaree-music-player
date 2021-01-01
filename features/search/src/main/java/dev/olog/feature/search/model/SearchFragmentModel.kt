package dev.olog.feature.search.model

import androidx.annotation.LayoutRes
import dev.olog.domain.mediaid.MediaId
import dev.olog.feature.search.R

sealed class SearchFragmentModel(
    @LayoutRes open val layoutType: Int
) {

    data class Track(
        val mediaId: MediaId,
        val title: String,
        val subtitle: String?
    ) : SearchFragmentModel(R.layout.item_search_song)

    data class Album(
        @LayoutRes private val layoutRes: Int,
        val mediaId: MediaId,
        val title: String,
        val subtitle: String?,
    ) : SearchFragmentModel(layoutRes)

    data class Header(
        val title: String,
        val subtitle: String,
    ) : SearchFragmentModel(R.layout.item_search_header)

    // recent

    data class RecentTrack(
        val mediaId: MediaId,
        val title: String,
        val subtitle: String?
    ) : SearchFragmentModel(R.layout.item_search_recent)

    data class RecentAlbum(
        @LayoutRes private val layoutRes: Int,
        val mediaId: MediaId,
        val title: String,
        val subtitle: String
    ) : SearchFragmentModel(layoutRes)

    object RecentHeader : SearchFragmentModel(R.layout.item_search_recent_header)

    object ClearRecent: SearchFragmentModel(R.layout.item_search_clear_recent)

    // list

    object FoldersList : SearchFragmentModel(R.layout.item_search_list_folders)
    object AlbumsList : SearchFragmentModel(R.layout.item_search_list_albums)
    object ArtistsList : SearchFragmentModel(R.layout.item_search_list_artists)
    object PlaylistList : SearchFragmentModel(R.layout.item_search_list_playlists)
    object GenreList : SearchFragmentModel(R.layout.item_search_list_genres)

}