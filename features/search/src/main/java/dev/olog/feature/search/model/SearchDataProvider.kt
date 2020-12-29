package dev.olog.feature.search.model

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.gateway.RecentSearchesGateway
import dev.olog.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.core.gateway.podcast.PodcastArtistGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.*
import dev.olog.feature.search.SearchFragmentHeaders
import dev.olog.shared.mapListItem
import kotlinx.coroutines.flow.*
import javax.inject.Inject

internal class SearchDataProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val searchHeaders: SearchFragmentHeaders,
    private val folderGateway: FolderGateway,
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
    private val recentSearchesGateway: RecentSearchesGateway

) {

    private val queryPublisher = MutableStateFlow("")

    fun updateQuery(query: String) {
        queryPublisher.value = query
    }

    fun observe(): Flow<List<SearchFragmentModel>> {
        return queryPublisher.flatMapLatest { query ->
            if (query.isBlank()) {
                getRecent()
            } else {
                getFiltered(query)
            }
        }
    }

    fun observeArtists(): Flow<List<SearchFragmentModel.Album>> {
        return queryPublisher.flatMapLatest(this::getArtists)
    }

    fun observeAlbums(): Flow<List<SearchFragmentModel.Album>> {
        return queryPublisher.flatMapLatest(this::getAlbums)
    }

    fun observeGenres(): Flow<List<SearchFragmentModel.Album>> {
        return queryPublisher.flatMapLatest(this::getGenres)
    }

    fun observePlaylists(): Flow<List<SearchFragmentModel.Album>> {
        return queryPublisher.flatMapLatest(this::getPlaylists)
    }

    fun observeFolders(): Flow<List<SearchFragmentModel.Album>> {
        return queryPublisher.flatMapLatest(this::getFolders)
    }

    private fun getRecent(): Flow<List<SearchFragmentModel>> {
        return recentSearchesGateway.getAll()
            .mapListItem { it.toSearchDisplayableItem(context) }
            .map { recent ->
                buildList {
                    if (recent.isNotEmpty()) {
                        add(SearchFragmentModel.ClearRecent)
                        addAll(recent)
                        add(0, searchHeaders.recent)
                    }
                }
            }
    }

    private fun getFiltered(query: String): Flow<List<SearchFragmentModel>> {
        return combine(
                getArtists(query).map { if (it.isNotEmpty()) searchHeaders.artistsHeaders(it.size) else it },
                getAlbums(query).map { if (it.isNotEmpty()) searchHeaders.albumsHeaders(it.size) else it },
                getPlaylists(query).map { if (it.isNotEmpty()) searchHeaders.playlistsHeaders(it.size) else it },
                getGenres(query).map { if (it.isNotEmpty()) searchHeaders.genreHeaders(it.size) else it },
                getFolders(query).map { if (it.isNotEmpty()) searchHeaders.foldersHeaders(it.size) else it },
                getSongs(query)
            ) { list -> list.toList().flatten() }
    }

    private fun getSongs(query: String): Flow<List<SearchFragmentModel>> {
        val tracksFlow = songGateway.observeAll().map { list ->
            list.filterBy(query).map { it.toSearchDisplayableItem() }
        }
        val podcastsFlow = podcastGateway.observeAll().map { list ->
            list.filterBy(query).map { it.toSearchDisplayableItem() }
        }
        return tracksFlow.combine(podcastsFlow) { tracks, podcasts ->
            val result = (tracks + podcasts).sortedBy { it.title }
            buildList {
                if (result.isNotEmpty()) {
                    add(searchHeaders.songsHeaders(result.size))
                    addAll(result)
                }
            }
        }
    }

    private fun getAlbums(query: String): Flow<List<SearchFragmentModel.Album>> {
        val tracksFlow = albumGateway.observeAll().map { list ->
            list.filterBy(query).map { it.toSearchDisplayableItem() }
        }
        val podcastsFlow = podcastAlbumGateway.observeAll().map { list ->
            list.filterBy(query).map { it.toSearchDisplayableItem() }
        }
        return tracksFlow.combine(podcastsFlow) { tracks, podcasts ->
            (tracks + podcasts).sortedBy { it.title }
        }
    }

    private fun getArtists(query: String): Flow<List<SearchFragmentModel.Album>> {
        val tracksFlow = artistGateway.observeAll().map { list ->
            list.filterBy(query).map { it.toSearchDisplayableItem() }
        }
        val podcastsFlow = podcastArtistGateway.observeAll().map { list ->
            list.filterBy(query).map { it.toSearchDisplayableItem() }
        }
        return tracksFlow.combine(podcastsFlow) { tracks, podcasts ->
            (tracks + podcasts).sortedBy { it.title }
        }
    }

    private fun getPlaylists(query: String): Flow<List<SearchFragmentModel.Album>> {
        val tracksFlow = playlistGateway.observeAll().map { list ->
            list.filterBy(query).map { it.toSearchDisplayableItem() }
        }
        val podcastsFlow = podcastPlaylistGateway.observeAll().map { list ->
            list.filterBy(query).map { it.toSearchDisplayableItem() }
        }
        return tracksFlow.combine(podcastsFlow) { tracks, podcasts ->
            (tracks + podcasts).sortedBy { it.title }
        }
    }

    private fun getGenres(query: String): Flow<List<SearchFragmentModel.Album>> {
        return genreGateway.observeAll().map { list ->
            list.filterBy(query).map { it.toSearchDisplayableItem() }
        }
    }

    private fun getFolders(query: String): Flow<List<SearchFragmentModel.Album>> {
        return folderGateway.observeAll().map { list ->
            list.filterBy(query).map { it.toSearchDisplayableItem() }
        }
    }

}