package dev.olog.presentation.search

import android.content.Context
import dev.olog.core.gateway.RecentSearchesGateway
import dev.olog.core.gateway.podcast.PodcastAuthorGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.*
import dev.olog.presentation.PresentationId.Companion.headerId
import dev.olog.presentation.R
import dev.olog.presentation.model.DisplayableAlbum
import dev.olog.presentation.model.DisplayableHeader
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.ApplicationContext
import dev.olog.shared.mapListItem
import dev.olog.shared.startWithIfNotEmpty
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class SearchDataProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val searchHeaders: SearchFragmentHeaders,
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val trackGateway: TrackGateway,
    private val albumGateway: AlbumGateway,
    private val artistGateway: ArtistGateway,
    private val genreGateway: GenreGateway,
    // podcasts
    private val podcastPlaylistGateway: PodcastPlaylistGateway,
    private val podcastAuthorGateway: PodcastAuthorGateway,
    // recent
    private val recentSearchesGateway: RecentSearchesGateway

) {

    private val queryChannel = ConflatedBroadcastChannel("")

    fun updateQuery(query: String) {
        queryChannel.offer(query)
    }

    fun dispose() {
        queryChannel.close()
    }

    fun observe(showPodcast: Boolean): Flow<List<DisplayableItem>> {
        return queryChannel.asFlow().flatMapLatest { query ->
            if (query.isBlank()) {
                getRecents(showPodcast)
            } else {
                getFiltered(query, showPodcast)
            }
        }
    }

    fun observeArtists(showPodcast: Boolean): Flow<List<DisplayableAlbum>> {
        return queryChannel.asFlow()
            .flatMapLatest { getArtists(it, showPodcast) }
    }

    fun observeAlbums(showPodcast: Boolean): Flow<List<DisplayableAlbum>> {
        return queryChannel.asFlow()
            .flatMapLatest { getAlbums(it, showPodcast) }
    }

    fun observeGenres(showPodcast: Boolean): Flow<List<DisplayableAlbum>> {
        return queryChannel.asFlow()
            .flatMapLatest { getGenres(it, showPodcast) }
    }

    fun observePlaylists(showPodcast: Boolean): Flow<List<DisplayableAlbum>> {
        return queryChannel.asFlow()
            .flatMapLatest { getPlaylists(it, showPodcast) }
    }

    fun observeFolders(showPodcast: Boolean): Flow<List<DisplayableAlbum>> {
        return queryChannel.asFlow()
            .flatMapLatest { getFolders(it, showPodcast) }
    }

    private fun getRecents(showPodcast: Boolean): Flow<List<DisplayableItem>> {
        return recentSearchesGateway.getAll()
            .map { list ->
                list.filter {if (showPodcast) it.mediaId.isAnyPodcast else !it.mediaId.isAnyPodcast }
            }
            .mapListItem { it.toSearchDisplayableItem(context) }
            .map { it.toMutableList() }
            .map {
                if (it.isNotEmpty()) {
                    it.add(
                        DisplayableHeader(
                            type = R.layout.item_search_clear_recent,
                            mediaId = headerId("clear recent"),
                            title = ""
                        )
                    )
                    it.addAll(0, searchHeaders.recents)
                }
                it
            }
    }

    private fun getFiltered(query: String, showPodcast: Boolean): Flow<List<DisplayableItem>> {
        return combine(
                getArtists(query, showPodcast).map { if (it.isNotEmpty()) searchHeaders.artistsHeaders(it.size, showPodcast) else it },
                getAlbums(query, showPodcast).map { if (it.isNotEmpty()) searchHeaders.albumsHeaders(it.size) else it },
                getPlaylists(query, showPodcast).map { if (it.isNotEmpty()) searchHeaders.playlistsHeaders(it.size) else it },
                getGenres(query, showPodcast).map { if (it.isNotEmpty()) searchHeaders.genreHeaders(it.size) else it },
                getFolders(query, showPodcast).map { if (it.isNotEmpty()) searchHeaders.foldersHeaders(it.size) else it },
                getSongs(query, showPodcast)
            ) { list -> list.toList().flatten() }
    }

    private fun getSongs(query: String, showPodcast: Boolean): Flow<List<DisplayableItem>> {
        return if (showPodcast) {
            trackGateway.observeAllPodcasts()
        } else {
            trackGateway.observeAllTracks()
        }.map { list ->
            val result = list.asSequence()
                .filter {
                    it.title.contains(query, true) ||
                            it.artist.contains(query, true) ||
                            it.album.contains(query, true)
                }.map { it.toSearchDisplayableItem() }
                .toList()
                .sortedBy { it.title }

            result.startWithIfNotEmpty(searchHeaders.trackHeaders(result.size, showPodcast))
        }
    }

    private fun getAlbums(query: String, showPodcast: Boolean): Flow<List<DisplayableAlbum>> {
        if (showPodcast) {
            return flowOf(emptyList())
        }
        return albumGateway.observeAll().map { list ->
            if (query.isBlank()) {
                return@map listOf<DisplayableAlbum>()
            }
            list.asSequence()
                .filter {
                    it.title.contains(query, true) || it.artist.contains(query, true)
                }.map { it.toSearchDisplayableItem() }
                .toList()
                .sortedBy { it.title }
        }
    }

    private fun getArtists(query: String, showPodcast: Boolean): Flow<List<DisplayableAlbum>> {
        return if (showPodcast) {
            podcastAuthorGateway.observeAll()
        } else {
            artistGateway.observeAll()
        }.map { list ->
            if (query.isBlank()) {
                return@map listOf<DisplayableAlbum>()
            }
            list.asSequence()
                .filter { it.name.contains(query, true) }
                .map { it.toSearchDisplayableItem() }
                .toList()
                .sortedBy { it.title }
        }
    }

    private fun getPlaylists(query: String, showPodcast: Boolean): Flow<List<DisplayableAlbum>> {
        return if (showPodcast) {
            podcastPlaylistGateway.observeAll()
        } else {
            playlistGateway.observeAll()
        }.map { list ->
            if (query.isBlank()) {
                return@map listOf<DisplayableAlbum>()
            }

            list.asSequence()
                .filter {
                    it.title.contains(query, true)
                }.map { it.toSearchDisplayableItem() }
                .toList()
                .sortedBy { it.title }
        }
    }

    private fun getGenres(query: String, showPodcast: Boolean): Flow<List<DisplayableAlbum>> {
        if (showPodcast) {
            return flowOf(emptyList())
        }
        return genreGateway.observeAll().map { list ->
            if (query.isBlank()) {
                return@map listOf<DisplayableAlbum>()
            }
            list.asSequence()
                .filter {
                    it.name.contains(query, true)
                }.map { it.toSearchDisplayableItem() }
                .toList()
        }
    }

    private fun getFolders(query: String, showPodcast: Boolean): Flow<List<DisplayableAlbum>> {
        if (showPodcast) {
            return flowOf(emptyList())
        }
        return folderGateway.observeAll().map { list ->
            if (query.isBlank()) {
                return@map listOf<DisplayableAlbum>()
            }
            list.asSequence()
                .filter {
                    it.title.contains(query, true)
                }.map { it.toSearchDisplayableItem() }
                .toList()
        }
    }

}