package dev.olog.presentation.search

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.MediaIdCategory
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
import dev.olog.presentation.search.adapter.SearchFragmentItem
import dev.olog.shared.mapListItem
import dev.olog.shared.startWithIfNotEmpty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
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

    fun observe(): Flow<List<SearchFragmentItem>> {
        return queryChannel.flatMapLatest { query ->
            if (query.isBlank()) {
                getRecents()
            } else {
                getFiltered(query)
            }
        }
    }

    private fun getRecents(): Flow<List<SearchFragmentItem>> {
        return recentSearchesGateway.getAll()
            .mapListItem { it.toSearchDisplayableItem(context) }
            .map { list ->
                buildList {
                    addAll(list)
                    if (list.isNotEmpty()) {
                        add(0, searchHeaders.recents)
                        add(SearchFragmentItem.ClearRecents)
                    }
                }
            }
    }

    private fun getFiltered(query: String): Flow<List<SearchFragmentItem>> {
        return combine(
                getArtists(query).map { searchHeaders.nestedListHeaders(MediaIdCategory.ARTISTS, it) },
                getAlbums(query).map { searchHeaders.nestedListHeaders(MediaIdCategory.ALBUMS, it) },
                getPlaylists(query).map { searchHeaders.nestedListHeaders(MediaIdCategory.PLAYLISTS, it) },
                getGenres(query).map { searchHeaders.nestedListHeaders(MediaIdCategory.GENRES, it) },
                getFolders(query).map { searchHeaders.nestedListHeaders(MediaIdCategory.FOLDERS, it) },
                getSongs(query)
            ) { list -> list.toList().flatten() }
    }

    private fun getSongs(query: String): Flow<List<SearchFragmentItem>> {
        if (query.isBlank()) {
            return flowOf(emptyList())
        }
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

    private fun getAlbums(query: String): Flow<List<SearchFragmentItem.Album>> {
        if (query.isBlank()) {
            return flowOf(emptyList())
        }
        return albumGateway.observeAll().map {
            it.asSequence()
                .filter {
                    it.title.contains(query, true) ||
                            it.artist.contains(query, true)
                }.map { it.toSearchDisplayableItem() }
                .toList()
        }.combine(
            podcastAlbumGateway.observeAll().map {
                it.asSequence()
                    .filter {
                        it.title.contains(query, true) ||
                                it.artist.contains(query, true)
                    }.map { it.toSearchDisplayableItem() }
                    .toList()
            }
        ) { track, podcast ->
            (track + podcast).sortedBy { it.title }
        }
    }

    private fun getArtists(query: String): Flow<List<SearchFragmentItem.Album>> {
        if (query.isBlank()) {
            return flowOf(emptyList())
        }
        return artistGateway.observeAll().map {
            it.asSequence()
                .filter {
                    it.name.contains(query, true)
                }.map { it.toSearchDisplayableItem() }
                .toList()
        }.combine(
            podcastArtistGateway.observeAll().map {
                it.asSequence()
                    .filter {
                        it.name.contains(query, true)
                    }.map { it.toSearchDisplayableItem() }
                    .toList()
            }
        ) { track, podcast ->
            (track + podcast).sortedBy { it.title }
        }
    }

    private fun getPlaylists(query: String): Flow<List<SearchFragmentItem.Album>> {
        if (query.isBlank()) {
            return flowOf(emptyList())
        }
        return playlistGateway2.observeAll().map {
            it.asSequence()
                .filter {
                    it.title.contains(query, true)
                }.map { it.toSearchDisplayableItem() }
                .toList()
        }.combine(
            podcastPlaylistGateway.observeAll().map {
                it.asSequence()
                    .filter {
                        it.title.contains(query, true)
                    }.map { it.toSearchDisplayableItem() }
                    .toList()
            }
        ) { track, podcast ->
            (track + podcast).sortedBy { it.title }
        }
    }

    private fun getGenres(query: String): Flow<List<SearchFragmentItem.Album>> {
        if (query.isBlank()) {
            return flowOf(emptyList())
        }
        return genreGateway.observeAll().map {
            it.asSequence()
                .filter {
                    it.name.contains(query, true)
                }.map { it.toSearchDisplayableItem() }
                .toList()
        }
    }

    private fun getFolders(query: String): Flow<List<SearchFragmentItem.Album>> {
        if (query.isBlank()) {
            return flowOf(emptyList())
        }
        return folderGateway.observeAll().map {
            it.asSequence()
                .filter {
                    it.title.contains(query, true)
                }.map { it.toSearchDisplayableItem() }
                .toList()
        }
    }

}