package dev.olog.feature.search.model

import dev.olog.core.MediaId

sealed class SearchState {

    data class Recents(
        val items: List<SearchRecentItem>
    ) : SearchState()

    data class Items(
        val playlists: List<SearchItem>,
        val albums: List<SearchItem>,
        val artists: List<SearchItem>,
        val genres: List<SearchItem>,
        val tracks: List<SearchItem>,
    ) : SearchState()

    object NoRecents : SearchState()
    object NoResults : SearchState()

    companion object {
        fun items(
            playlists: List<SearchItem>,
            albums: List<SearchItem>,
            artists: List<SearchItem>,
            genres: List<SearchItem>,
            tracks: List<SearchItem>,
        ): SearchState {
            if (playlists.isEmpty() && albums.isEmpty() && artists.isEmpty() && genres.isEmpty() && tracks.isEmpty()) {
                return NoResults
            }
            return Items(
                playlists = playlists,
                albums = albums,
                artists = artists,
                genres = genres,
                tracks = tracks
            )
        }

        fun recents(items: List<SearchRecentItem>): SearchState {
            if (items.isEmpty()) {
                return NoRecents
            }
            return Recents(items)
        }
    }

}

data class SearchItem(
    val mediaId: MediaId,
    val title: String,
    val subtitle: String?,
    val isPodcast: Boolean,
)

data class SearchRecentItem(
    val mediaId: MediaId,
    val title: String,
    val subtitle: String?,
    val isPlayable: Boolean,
    val isPodcast: Boolean,
)