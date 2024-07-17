package dev.olog.presentation.search

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.gateway.RecentSearchesGateway
import dev.olog.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.core.gateway.podcast.PodcastArtistGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.AlbumGateway
import dev.olog.core.gateway.track.ArtistGateway
import dev.olog.core.gateway.track.FolderGateway
import dev.olog.core.gateway.track.GenreGateway
import dev.olog.core.gateway.track.PlaylistGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.presentation.search.adapter.SearchItem
import dev.olog.shared.mapListItem
import dev.olog.shared.startWithIfNotEmpty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchDataProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val searchHeaders: SearchFragmentHeaders,
    private val folderGateway: FolderGateway,
    private val playlistGateway2: PlaylistGateway,
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
    private val recentSearchesGateway: RecentSearchesGateway

) {

    private val queryChannel = MutableStateFlow("")

    fun updateQuery(query: String) {
        queryChannel.value = query
    }

    fun observe(): Flow<List<SearchItem>> {
        return queryChannel.flatMapLatest { query ->
            if (query.isBlank()) {
                getRecents()
            } else {
                getFiltered(query)
            }
        }
    }

    private fun getRecents(): Flow<List<SearchItem>> {
        return recentSearchesGateway.getAll()
            .mapListItem { it.toSearchDisplayableItem(context) }
            .map { it.toMutableList() }
            .map {
                if (it.isNotEmpty()) {
                    it.add(SearchItem.ClearRecents)
                    it.add(0, searchHeaders.recents)
                }
                it
            }
    }

    private fun getFiltered(query: String): Flow<List<SearchItem>> {
        return combine(
                getArtists(query).map { if (it.isNotEmpty()) searchHeaders.artistsHeaders(it) else emptyList() },
                getAlbums(query).map { if (it.isNotEmpty()) searchHeaders.albumsHeaders(it) else emptyList() },
                getPlaylists(query).map { if (it.isNotEmpty()) searchHeaders.playlistsHeaders(it) else emptyList() },
                getGenres(query).map { if (it.isNotEmpty()) searchHeaders.genreHeaders(it) else emptyList() },
                getFolders(query).map { if (it.isNotEmpty()) searchHeaders.foldersHeaders(it) else emptyList() },
                getSongs(query)
            ) { list -> list.toList().flatten() }
    }

    private fun getSongs(query: String): Flow<List<SearchItem>> {
        return songGateway.observeAll().map {
            it.asSequence()
                .filter {
                    it.title.contains(query, true) ||
                            it.artist.contains(query, true) ||
                            it.album.contains(query, true)
                }.map { it.toSearchDisplayableItem() }
                .toList()
        }.combine(
            podcastGateway.observeAll().map {
                it.asSequence()
                    .filter {
                        it.title.contains(query, true) ||
                                it.artist.contains(query, true) ||
                                it.album.contains(query, true)
                    }.map { it.toSearchDisplayableItem() }
                    .toList()
            }
        ) { track, podcast ->
            val result = (track + podcast).sortedBy { it.title }
            result.startWithIfNotEmpty(searchHeaders.songsHeaders(result.size))
        }
    }

    private fun getAlbums(query: String): Flow<List<SearchItem.Album>> {
        return albumGateway.observeAll().map {
            if (query.isBlank()) {
                return@map emptyList()
            }
            it.asSequence()
                .filter {
                    it.title.contains(query, true) ||
                            it.artist.contains(query, true)
                }.map { it.toSearchDisplayableItem() }
                .toList()
        }.combine(
            podcastAlbumGateway.observeAll().map {
                if (query.isBlank()) {
                    return@map emptyList()
                }
                it.asSequence()
                    .filter {
                        it.title.contains(query, true) ||
                                it.artist.contains(query, true)
                    }.map { it.toSearchDisplayableItem() }
                    .toList()
            }
        ) { track, podcast ->
            (track + podcast)
                .sortedBy { it.title }
        }
    }

    private fun getArtists(query: String): Flow<List<SearchItem.Album>> {
        return artistGateway.observeAll().map {
            if (query.isBlank()) {
                return@map emptyList()
            }
            it.asSequence()
                .filter {
                    it.name.contains(query, true)
                }.map { it.toSearchDisplayableItem() }
                .toList()
        }.combine(
            podcastArtistGateway.observeAll().map {
                if (query.isBlank()) {
                    return@map emptyList()
                }
                it.asSequence()
                    .filter {
                        it.name.contains(query, true)
                    }.map { it.toSearchDisplayableItem() }
                    .toList()
            }
        ) { track, podcast ->
            (track + podcast)
                .sortedBy { it.title }
        }
    }

    private fun getPlaylists(query: String): Flow<List<SearchItem.Album>> {
        return playlistGateway2.observeAll().map {
            if (query.isBlank()) {
                return@map emptyList()
            }
            it.asSequence()
                .filter {
                    it.title.contains(query, true)
                }.map { it.toSearchDisplayableItem() }
                .toList()
        }.combine(
            podcastPlaylistGateway.observeAll().map {
                if (query.isBlank()) {
                    return@map emptyList()
                }

                it.asSequence()
                    .filter {
                        it.title.contains(query, true)
                    }.map { it.toSearchDisplayableItem() }
                    .toList()
            }
        ) { track, podcast ->
            (track + podcast)
                .sortedBy { it.title }
        }
    }

    private fun getGenres(query: String): Flow<List<SearchItem.Album>> {
        return genreGateway.observeAll().map {
            if (query.isBlank()) {
                return@map emptyList()
            }
            it.asSequence()
                .filter {
                    it.name.contains(query, true)
                }.map { it.toSearchDisplayableItem() }
                .toList()
        }
    }

    private fun getFolders(query: String): Flow<List<SearchItem.Album>> {
        return folderGateway.observeAll().map {
            if (query.isBlank()) {
                return@map emptyList()
            }
            it.asSequence()
                .filter {
                    it.title.contains(query, true)
                }.map { it.toSearchDisplayableItem() }
                .toList()
        }
    }

}