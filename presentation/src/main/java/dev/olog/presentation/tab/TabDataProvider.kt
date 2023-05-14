package dev.olog.presentation.tab

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.gateway.QueryMode
import dev.olog.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.core.gateway.podcast.PodcastArtistGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.AlbumGateway
import dev.olog.core.gateway.track.ArtistGateway
import dev.olog.core.gateway.track.AutoPlaylistGateway
import dev.olog.core.gateway.track.FolderGateway
import dev.olog.core.gateway.track.GenreGateway
import dev.olog.core.gateway.track.PlaylistGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.presentation.tab.mapper.toAutoPlaylist
import dev.olog.presentation.tab.mapper.toTabDisplayableItem
import dev.olog.presentation.tab.mapper.toTabLastPlayedDisplayableItem
import dev.olog.shared.doIf
import dev.olog.shared.mapListItem
import dev.olog.shared.startWithIfNotEmpty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class TabDataProvider @Inject constructor(
    @ApplicationContext context: Context,
    private val headers: TabFragmentHeaders,
    // songs
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val songGateway: SongGateway,
    private val albumGateway: AlbumGateway,
    private val artistGateway: ArtistGateway,
    private val genreGateway: GenreGateway,
    // podcast
    private val podcastGateway: PodcastGateway,
    private val podcastAlbumGateway: PodcastAlbumGateway,
    private val podcastArtistGateway: PodcastArtistGateway,
    private val presentationPrefs: PresentationPreferencesGateway,
    private val autoPlaylistGateway: AutoPlaylistGateway,
) {

    private val resources = context.resources

    fun get(category: TabCategory, isPodcast: Boolean): Flow<List<DisplayableItem>> = when (category) {
        // songs
        TabCategory.FOLDERS -> getFolders()
        TabCategory.PLAYLISTS -> {
            if (isPodcast) getPlaylist(QueryMode.Podcasts) else getPlaylist(QueryMode.Songs)
        }
        TabCategory.SONGS -> {
            if (isPodcast) {
                podcastGateway.observeAll().map {
                    it.map { it.toTabDisplayableItem() }.startWithIfNotEmpty(headers.shuffleHeader)
                }
            } else {
                songGateway.observeAll().map {
                    it.map { it.toTabDisplayableItem() }.startWithIfNotEmpty(headers.shuffleHeader)
                }
            }
        }
        TabCategory.ALBUMS -> {
            if (isPodcast) getPodcastAlbums() else getAlbums()
        }
        TabCategory.ARTISTS -> if (isPodcast) getPodcastArtists() else getArtists()
        TabCategory.GENRES -> getGenres()
        // podcasts
    }.flowOn(Dispatchers.Default)

    fun getRecentlyAddedAlbums(isPodcast: Boolean): Flow<List<DisplayableItem>> {
        if (isPodcast) {
            return podcastAlbumGateway.observeRecentlyAdded().mapListItem { it.toTabLastPlayedDisplayableItem() }
        }
        return albumGateway.observeRecentlyAdded().mapListItem { it.toTabLastPlayedDisplayableItem() }
    }

    fun getRecentlyAddedArtists(isPodcast: Boolean): Flow<List<DisplayableItem>> {
        if (isPodcast) {
            return podcastArtistGateway.observeRecentlyAdded().mapListItem {
                it.toTabLastPlayedDisplayableItem(resources)
            }
        }
        return artistGateway.observeRecentlyAdded().mapListItem {
            it.toTabLastPlayedDisplayableItem(resources)
        }
    }

    fun getRecentlyPlayedAlbums(isPodcast: Boolean): Flow<List<DisplayableItem>> {
        if (isPodcast) {
            return podcastAlbumGateway.observeRecentlyPlayed().mapListItem { it.toTabLastPlayedDisplayableItem() }
        }
        return albumGateway.observeRecentlyPlayed().mapListItem { it.toTabLastPlayedDisplayableItem() }
    }

    fun getRecentlyPlayedArtists(isPodcast: Boolean): Flow<List<DisplayableItem>> {
        if (isPodcast) {
            return podcastArtistGateway.observeRecentlyPlayed().mapListItem {
                it.toTabLastPlayedDisplayableItem(resources)
            }
        }
        return artistGateway.observeRecentlyPlayed().mapListItem {
            it.toTabLastPlayedDisplayableItem(resources)
        }
    }

    private fun getFolders(): Flow<List<DisplayableItem>> {
        return folderGateway.observeAll()
            .map { folders ->
                val requestedSpanSize = presentationPrefs.getSpanCount(TabCategory.FOLDERS)
                folders.map { it.toTabDisplayableItem(resources, requestedSpanSize) }
            }
    }

    private fun getGenres(): Flow<List<DisplayableItem>> {
        return genreGateway.observeAll()
            .map { genres ->
                val requestedSpanSize = presentationPrefs.getSpanCount(TabCategory.GENRES)
                genres.map { it.toTabDisplayableItem(resources, requestedSpanSize) }
            }
    }

    private fun getPlaylist(mode: QueryMode): Flow<List<DisplayableItem>> {
        val requestedSpanSize = presentationPrefs.getSpanCount(TabCategory.PLAYLISTS)
        return combine(
            autoPlaylistGateway.observeAll(mode).mapListItem { it.toAutoPlaylist(resources) },
            playlistGateway.observeAll(mode).mapListItem { it.toTabDisplayableItem(resources, requestedSpanSize) },
        ) { autoPlaylists, playlists ->
            buildList {
                this += headers.autoPlaylistHeader
                this += autoPlaylists
                if (playlists.isNotEmpty()) {
                    this += headers.allPlaylistHeader
                    this += playlists
                }
            }
        }
    }

    private fun getAlbums(): Flow<List<DisplayableItem>> {
        val recentlyAddedFlow = albumGateway.observeRecentlyAdded()
            .combine(presentationPrefs.observeLibraryNewVisibility()) { data, canShow ->
                if (canShow) data else emptyList()
            }
        val recentlyPlayedFlow = albumGateway.observeRecentlyPlayed()
            .combine(presentationPrefs.observeLibraryRecentPlayedVisibility()) { data, canShow ->
                if (canShow) data else emptyList()
            }

        return combine(
            albumGateway.observeAll()
                .map { albums ->
                    val requestedSpanSize = presentationPrefs.getSpanCount(TabCategory.ALBUMS)
                    albums.map { it.toTabDisplayableItem(requestedSpanSize) }
                },
            recentlyAddedFlow,
            recentlyPlayedFlow
        ) { all, recentlyAdded, lastPlayed ->
            val result = mutableListOf<DisplayableItem>()
            result.doIf(recentlyAdded.count() > 0) { addAll(headers.recentlyAddedAlbumsHeaders) }
                .doIf(lastPlayed.count() > 0) { addAll(headers.lastPlayedAlbumHeaders) }
                .doIf(result.isNotEmpty()) { addAll(headers.allAlbumsHeader) }
                .plus(all)
        }
    }

    private fun getArtists(): Flow<List<DisplayableItem>> {
        val recentlyAddedFlow = artistGateway.observeRecentlyAdded()
            .combine(presentationPrefs.observeLibraryNewVisibility()) { data, canShow ->
                if (canShow) data else emptyList()
            }
        val recentlyPlayedFlow = artistGateway.observeRecentlyPlayed()
            .combine(presentationPrefs.observeLibraryRecentPlayedVisibility()) { data, canShow ->
                if (canShow) data else emptyList()
            }

        return combine(
            artistGateway.observeAll()
                .map { artists ->
                    val requestedSpanSize = presentationPrefs.getSpanCount(TabCategory.ARTISTS)
                    artists.map { it.toTabDisplayableItem(resources, requestedSpanSize) }
                },
            recentlyAddedFlow,
            recentlyPlayedFlow
        ) { all, recentlyAdded, lastPlayed ->
            val result = mutableListOf<DisplayableItem>()
            result.doIf(recentlyAdded.count() > 0) { addAll(headers.recentlyAddedArtistsHeaders) }
                .doIf(lastPlayed.count() > 0) { addAll(headers.lastPlayedArtistHeaders) }
                .doIf(result.isNotEmpty()) { addAll(headers.allArtistsHeader) }
                .plus(all)
        }
    }

    private fun getPodcastAlbums(): Flow<List<DisplayableItem>> {
        val recentlyAddedFlow = podcastAlbumGateway.observeRecentlyAdded()
            .combine(presentationPrefs.observeLibraryNewVisibility()) { data, canShow ->
                if (canShow) data else emptyList()
            }
        val recentlyPlayedFlow = podcastAlbumGateway.observeRecentlyPlayed()
            .combine(presentationPrefs.observeLibraryRecentPlayedVisibility()) { data, canShow ->
                if (canShow) data else emptyList()
            }

        return combine(
            podcastAlbumGateway.observeAll()
                .map { albums ->
                    val requestedSpanSize =
                        presentationPrefs.getSpanCount(TabCategory.ALBUMS)
                    albums.map { it.toTabDisplayableItem(requestedSpanSize) }
                },
            recentlyAddedFlow,
            recentlyPlayedFlow
        ) { all, recentlyAdded, lastPlayed ->
            val result = mutableListOf<DisplayableItem>()
            result.doIf(recentlyAdded.count() > 0) { addAll(headers.recentlyAddedAlbumsHeaders) }
                .doIf(lastPlayed.count() > 0) { addAll(headers.lastPlayedAlbumHeaders) }
                .doIf(result.isNotEmpty()) { addAll(headers.allAlbumsHeader) }
                .plus(all)
        }
    }

    private fun getPodcastArtists(): Flow<List<DisplayableItem>> {
        val recentlyAddedFlow = podcastArtistGateway.observeRecentlyAdded()
            .combine(presentationPrefs.observeLibraryNewVisibility()) { data, canShow ->
                if (canShow) data else emptyList()
            }
        val recentlyPlayedFlow = podcastArtistGateway.observeRecentlyPlayed()
            .combine(presentationPrefs.observeLibraryRecentPlayedVisibility()) { data, canShow ->
                if (canShow) data else emptyList()
            }

        return combine(
            podcastArtistGateway.observeAll()
                .map { artists ->
                    val requestedSpanSize =
                        presentationPrefs.getSpanCount(TabCategory.ARTISTS)
                    artists.map { it.toTabDisplayableItem(resources, requestedSpanSize) }
                },
            recentlyAddedFlow,
            recentlyPlayedFlow
        ) { all, recentlyAdded, lastPlayed ->
            val result = mutableListOf<DisplayableItem>()
            result.doIf(recentlyAdded.count() > 0) { addAll(headers.recentlyAddedArtistsHeaders) }
                .doIf(lastPlayed.count() > 0) { addAll(headers.lastPlayedArtistHeaders) }
                .doIf(result.isNotEmpty()) { addAll(headers.allArtistsHeader) }
                .plus(all)
        }
    }
}