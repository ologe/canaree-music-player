package dev.olog.feature.search

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.gateway.RecentSearchesGateway
import dev.olog.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.core.gateway.podcast.PodcastArtistGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.AlbumGateway
import dev.olog.core.gateway.track.ArtistGateway
import dev.olog.core.gateway.track.GenreGateway
import dev.olog.core.gateway.track.PlaylistGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.core.schedulers.Schedulers
import dev.olog.feature.search.model.SearchItem
import dev.olog.feature.search.model.SearchState
import dev.olog.feature.search.model.toSearchDisplayableItem
import dev.olog.shared.TextUtils
import dev.olog.shared.extension.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

class SearchDataProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val playlistGateway: PlaylistGateway,
    private val songGateway: SongGateway,
    private val albumGateway: AlbumGateway,
    private val artistGateway: ArtistGateway,
    private val genreGateway: GenreGateway,
    // podcasts
    private val podcastPlaylistGateway: PodcastPlaylistGateway,
    private val podcastGateway: PodcastGateway,
    private val podcastAlbumGateway: PodcastAlbumGateway,
    private val podcastArtistGateway: PodcastArtistGateway,
    // recent
    private val recentSearchesGateway: RecentSearchesGateway,
    private val schedulers: Schedulers,
) {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String>
        get() = _query

    fun updateQuery(query: String) {
        _query.value = query
    }

    fun observe(): Flow<SearchState> {
        return _query
            .debounce(200) // todo check if is corret
            .filter { it.isBlank() || it.trim().length >= 2 }
            .flatMapLatest { query ->
                if (query.isBlank()) {
                    getRecents()
                } else {
                    getFiltered(query)
                }
            }
            .flowOn(schedulers.cpu)
    }


    private fun getRecents(): Flow<SearchState> {
        return recentSearchesGateway.getAll()
            .mapListItem { it.toSearchDisplayableItem(context) }
            .map(SearchState::recents)
    }

    // todo include folders?
    private fun getFiltered(query: String): Flow<SearchState> {
        return combine(
            getPlaylists(query),
            getAlbums(query),
            getArtists(query),
            getGenres(query),
            getSongs(query)
        ) { list ->
            SearchState.items(
                playlists = list[0],
                albums = list[1],
                artists = list[2],
                genres = list[3],
                tracks = list[4],
            )
        }
    }

    private fun getSongs(query: String): Flow<List<SearchItem>> {
        return combine(songGateway.observeAll(), podcastGateway.observeAll(), ::merge)
            .mapLatest { list ->
                list.asSequence()
                    .filter {
                        it.title.contains(query, true) ||
                            it.artist.contains(query, true) ||
                            it.album.contains(query, true)
                    }.map {
                        SearchItem(
                            mediaId = it.getMediaId(),
                            title = it.title,
                            subtitle = TextUtils.getTrackText(it.artist, it.album),
                            isPodcast = it.isPodcast,
                        )
                    }
                    .sortedBy { it.title.lowercase() }
                    .toList()
            }
    }

    private fun<T> merge(list1: List<T>, list2: List<T>) = list1 + list2

    private fun getAlbums(query: String): Flow<List<SearchItem>> {
        return combine(albumGateway.observeAll(), podcastAlbumGateway.observeAll(), ::merge)
            .mapLatest { list ->
                list.asSequence()
                    .filter {
                        it.title.contains(query, true) ||
                            it.artist.contains(query, true)
                    }.map {
                        SearchItem(
                            mediaId = it.getMediaId(),
                            title = it.title,
                            subtitle = it.artist,
                            isPodcast = it.isPodcast,
                        )
                    }
                    .sortedBy { it.title.lowercase() }
                    .toList()
            }
    }

    private fun getArtists(query: String): Flow<List<SearchItem>> {
        return combine(artistGateway.observeAll(), podcastArtistGateway.observeAll(), ::merge)
            .mapLatest { list ->
                list.asSequence()
                    .filter { it.name.contains(query, true) }
                    .map {
                        SearchItem(
                            mediaId = it.getMediaId(),
                            title = it.name,
                            subtitle = null,
                            isPodcast = it.isPodcast,
                        )
                    }
                    .sortedBy { it.title.lowercase() }
                    .toList()
            }
    }

    private fun getPlaylists(query: String): Flow<List<SearchItem>> {
        return combine(playlistGateway.observeAll(), podcastPlaylistGateway.observeAll(), ::merge)
            .mapLatest { list ->
                list.asSequence()
                    .filter { it.title.contains(query, true) }
                    .map {
                        SearchItem(
                            mediaId = it.getMediaId(),
                            title = it.title,
                            subtitle = null,
                            isPodcast = it.isPodcast,
                        )
                    }
                    .sortedBy { it.title.lowercase() }
                    .toList()
            }
    }

    private fun getGenres(query: String): Flow<List<SearchItem>> {
        return genreGateway.observeAll().mapLatest { list ->
            list.asSequence()
                .filter { it.name.contains(query, true) }
                .map {
                    SearchItem(
                        mediaId = it.getMediaId(),
                        title = it.name,
                        subtitle = null,
                        isPodcast = false,
                    )
                }
                .sortedBy { it.title.lowercase() }
                .toList()
        }
    }

}