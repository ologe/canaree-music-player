package dev.olog.msc.presentation.search

import android.content.Context
import dev.olog.core.MediaId
import dev.olog.core.RecentSearchesTypes
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.entity.SearchResult
import dev.olog.core.entity.track.*
import dev.olog.core.gateway.*
import dev.olog.msc.R
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.CustomScope
import dev.olog.shared.extensions.assertBackground
import dev.olog.shared.extensions.mapListItem
import dev.olog.shared.extensions.startWithIfNotEmpty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchDataProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val searchHeaders: SearchFragmentHeaders,
    private val folderGateway: FolderGateway,
    private val playlistGateway2: PlaylistGateway2,
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

) : CoroutineScope by CustomScope() {

    private val queryChannel = ConflatedBroadcastChannel("")

    fun updateQuery(query: String) {
        launch { queryChannel.send(query) }
    }

    fun observe(): Flow<List<DisplayableItem>> {
        return queryChannel.asFlow().switchMap { query ->
            if (query.isBlank()) {
                getRecents()
            } else {
                getFiltered(query)
            }
        }.assertBackground()
    }

    fun observeArtists(): Flow<List<DisplayableItem>> {
        return queryChannel.asFlow()
            .switchMap { getArtists(it) }
            .assertBackground()
    }

    fun observeAlbums(): Flow<List<DisplayableItem>> {
        return queryChannel.asFlow()
            .switchMap { getAlbums(it) }
            .assertBackground()
    }

    fun observeGenres(): Flow<List<DisplayableItem>> {
        return queryChannel.asFlow()
            .switchMap { getGenres(it) }
            .assertBackground()
    }

    fun observePlaylists(): Flow<List<DisplayableItem>> {
        return queryChannel.asFlow()
            .switchMap { getPlaylists(it) }
            .assertBackground()
    }

    fun observeFolders(): Flow<List<DisplayableItem>> {
        return queryChannel.asFlow()
            .switchMap { getFolders(it) }
            .assertBackground()
    }

    private fun getRecents(): Flow<List<DisplayableItem>> {
        return recentSearchesGateway.getAll()
            .mapListItem { it.toSearchDisplayableItem(context) }
            .map { it.toMutableList() }
            .map {
                if (it.isNotEmpty()) {
                    it.add(DisplayableItem(R.layout.item_search_clear_recent, MediaId.headerId("clear recent"), ""))
                    it.addAll(0, searchHeaders.recents)
                }
                it
            }
    }

    private fun getFiltered(query: String): Flow<List<DisplayableItem>> {
        return getArtists(query).map { if (it.isNotEmpty()) searchHeaders.artistsHeaders(it.size) else it }
            .combineLatest(
                getAlbums(query).map { if (it.isNotEmpty()) searchHeaders.albumsHeaders(it.size) else it },
                getPlaylists(query).map { if (it.isNotEmpty()) searchHeaders.playlistsHeaders(it.size) else it },
                getGenres(query).map { if (it.isNotEmpty()) searchHeaders.genreHeaders(it.size) else it },
                getFolders(query).map { if (it.isNotEmpty()) searchHeaders.foldersHeaders(it.size) else it },
                getSongs(query)
            ) { list -> list.flatMap { it } }
    }

    private fun getSongs(query: String): Flow<List<DisplayableItem>> {
        return songGateway.observeAll().map {
            it.asSequence()
                .filter {
                    it.title.contains(query, true) ||
                            it.artist.contains(query, true) ||
                            it.album.contains(query, true)
                }.map { it.toSearchDisplayableItem() }
                .toList()
        }.combineLatest(
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

    private fun getAlbums(query: String): Flow<List<DisplayableItem>> {
        return albumGateway.observeAll().map {
            if (query.isBlank()) {
                return@map listOf<DisplayableItem>()
            }
            it.asSequence()
                .filter {
                    it.title.contains(query, true) ||
                            it.artist.contains(query, true)
                }.map { it.toSearchDisplayableItem() }
                .toList()
        }.combineLatest(
            podcastAlbumGateway.observeAll().map {
                if (query.isBlank()) {
                    return@map listOf<DisplayableItem>()
                }
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

    private fun getArtists(query: String): Flow<List<DisplayableItem>> {
        return artistGateway.observeAll().map {
            if (query.isBlank()) {
                return@map listOf<DisplayableItem>()
            }
            it.asSequence()
                .filter {
                    it.name.contains(query, true)
                }.map { it.toSearchDisplayableItem() }
                .toList()
        }.combineLatest(
            podcastArtistGateway.observeAll().map {
                if (query.isBlank()) {
                    return@map listOf<DisplayableItem>()
                }
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

    private fun getPlaylists(query: String): Flow<List<DisplayableItem>> {
        return playlistGateway2.observeAll().map {
            if (query.isBlank()) {
                return@map listOf<DisplayableItem>()
            }
            it.asSequence()
                .filter {
                    it.title.contains(query, true)
                }.map { it.toSearchDisplayableItem() }
                .toList()
        }.combineLatest(
            podcastPlaylistGateway.observeAll().map {
                if (query.isBlank()) {
                    return@map listOf<DisplayableItem>()
                }

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

    private fun getGenres(query: String): Flow<List<DisplayableItem>> {
        return genreGateway.observeAll().map {
            if (query.isBlank()) {
                return@map listOf<DisplayableItem>()
            }
            it.asSequence()
                .filter {
                    it.name.contains(query, true)
                }.map { it.toSearchDisplayableItem() }
                .toList()
        }
    }

    private fun getFolders(query: String): Flow<List<DisplayableItem>> {
        return folderGateway.observeAll().map {
            if (query.isBlank()) {
                return@map listOf<DisplayableItem>()
            }
            it.asSequence()
                .filter {
                    it.title.contains(query, true)
                }.map { it.toSearchDisplayableItem() }
                .toList()
        }
    }

    private fun SearchResult.toSearchDisplayableItem(context: Context): DisplayableItem {
        val subtitle = when (this.itemType) {
            RecentSearchesTypes.SONG -> context.getString(R.string.search_type_track)
            RecentSearchesTypes.ALBUM -> context.getString(R.string.search_type_album)
            RecentSearchesTypes.ARTIST -> context.getString(R.string.search_type_artist)
            RecentSearchesTypes.PLAYLIST -> context.getString(R.string.search_type_playlist)
            RecentSearchesTypes.GENRE -> context.getString(R.string.search_type_genre)
            RecentSearchesTypes.FOLDER -> context.getString(R.string.search_type_folder)
            RecentSearchesTypes.PODCAST -> context.getString(R.string.search_type_podcast)
            RecentSearchesTypes.PODCAST_PLAYLIST -> context.getString(R.string.search_type_podcast_playlist)
            RecentSearchesTypes.PODCAST_ALBUM -> context.getString(R.string.search_type_podcast_album)
            RecentSearchesTypes.PODCAST_ARTIST -> context.getString(R.string.search_type_podcast_artist)
            else -> throw IllegalArgumentException("invalid item type $itemType")
        }

        val isPlayable = this.itemType == RecentSearchesTypes.SONG || this.itemType == RecentSearchesTypes.PODCAST

        val layout = when (this.itemType) {
            RecentSearchesTypes.ARTIST,
            RecentSearchesTypes.PODCAST_ARTIST -> R.layout.item_search_recent_artist
            RecentSearchesTypes.ALBUM,
            RecentSearchesTypes.PODCAST_ALBUM -> R.layout.item_search_recent_album
            else -> R.layout.item_search_recent
        }

        return DisplayableItem(
            layout,
            this.mediaId,
            this.title,
            subtitle,
            isPlayable
        )
    }

    private fun Song.toSearchDisplayableItem(): DisplayableItem {
        return DisplayableItem(
            R.layout.item_search_song,
            getMediaId(),
            title,
            DisplayableItem.adjustArtist(artist),
            true
        )
    }

    private fun Album.toSearchDisplayableItem(): DisplayableItem {
        return DisplayableItem(
            R.layout.item_search_album,
            getMediaId(),
            title,
            DisplayableItem.adjustArtist(artist)
        )
    }

    private fun Artist.toSearchDisplayableItem(): DisplayableItem {
        return DisplayableItem(
            R.layout.item_search_artist,
            getMediaId(),
            name,
            null
        )
    }

    private fun Playlist.toSearchDisplayableItem(): DisplayableItem {
        return DisplayableItem(
            R.layout.item_search_album,
            getMediaId(),
            title,
            null
        )
    }

    private fun Genre.toSearchDisplayableItem(): DisplayableItem {
        return DisplayableItem(
            R.layout.item_search_album,
            getMediaId(),
            name,
            null
        )
    }

    private fun Folder.toSearchDisplayableItem(): DisplayableItem {
        return DisplayableItem(
            R.layout.item_search_album,
            getMediaId(),
            title,
            null
        )
    }

}