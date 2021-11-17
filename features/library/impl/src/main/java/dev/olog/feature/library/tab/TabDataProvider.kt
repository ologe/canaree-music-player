package dev.olog.feature.library.tab

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.core.gateway.podcast.PodcastArtistGateway
import dev.olog.core.gateway.podcast.PodcastEpisodeGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.*
import dev.olog.feature.base.model.DisplayableItem
import dev.olog.feature.library.LibraryPrefs
import dev.olog.feature.library.TabCategory
import dev.olog.shared.doIf
import dev.olog.shared.mapListItem
import dev.olog.shared.startWith
import dev.olog.shared.startWithIfNotEmpty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TabDataProvider @Inject constructor(
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
    private val podcastPlaylistGateway: PodcastPlaylistGateway,
    private val podcastGateway: PodcastEpisodeGateway,
    private val podcastAlbumGateway: PodcastAlbumGateway,
    private val podcastArtistGateway: PodcastArtistGateway,
    private val libraryPrefs: LibraryPrefs
) {

    private val resources = context.resources

    fun get(category: TabCategory): Flow<List<DisplayableItem>> = when (category) {
        // songs
        TabCategory.FOLDERS -> getFolders()
        TabCategory.PLAYLISTS -> getPlaylist()
        TabCategory.SONGS -> songGateway.observeAll().map {
            it.map { it.toTabDisplayableItem() }.startWithIfNotEmpty(headers.shuffleHeader)
        }
        TabCategory.ALBUMS -> getAlbums()
        TabCategory.ARTISTS -> getArtists()
        TabCategory.GENRES -> getGenres()
        TabCategory.RECENTLY_ADDED_ALBUMS -> albumGateway.observeRecentlyAdded().mapListItem { it.toTabLastPlayedDisplayableItem() }
        TabCategory.RECENTLY_ADDED_ARTISTS -> artistGateway.observeRecentlyAdded().mapListItem {
            it.toTabLastPlayedDisplayableItem(
                resources
            )
        }
        TabCategory.LAST_PLAYED_ALBUMS -> albumGateway.observeLastPlayed().mapListItem { it.toTabLastPlayedDisplayableItem() }
        TabCategory.LAST_PLAYED_ARTISTS -> artistGateway.observeLastPlayed().mapListItem {
            it.toTabLastPlayedDisplayableItem(
                resources
            )
        }
        // podcasts
        TabCategory.PODCASTS_PLAYLIST -> getPodcastPlaylist()
        TabCategory.PODCASTS -> podcastGateway.observeAll().map {
            it.map { it.toTabDisplayableItem() }.startWithIfNotEmpty(headers.shuffleHeader)
        }
        TabCategory.PODCASTS_ALBUMS -> getPodcastAlbums()
        TabCategory.PODCASTS_ARTISTS -> getPodcastArtists()
        TabCategory.RECENTLY_ADDED_PODCAST_ALBUMS -> podcastAlbumGateway.observeRecentlyAdded().mapListItem { it.toTabLastPlayedDisplayableItem() }
        TabCategory.RECENTLY_ADDED_PODCAST_ARTISTS -> podcastArtistGateway.observeRecentlyAdded().mapListItem {
            it.toTabLastPlayedDisplayableItem(
                resources
            )
        }
        TabCategory.LAST_PLAYED_PODCAST_ALBUMS -> podcastAlbumGateway.observeLastPlayed().mapListItem { it.toTabLastPlayedDisplayableItem() }
        TabCategory.LAST_PLAYED_PODCAST_ARTISTS -> podcastArtistGateway.observeLastPlayed().mapListItem {
            it.toTabLastPlayedDisplayableItem(
                resources
            )
        }
    }.flowOn(Dispatchers.Default)

    private fun getFolders(): Flow<List<DisplayableItem>> {
        return folderGateway.observeAll()
            .map { folders ->
                val requestedSpanSize = libraryPrefs.spanCount(TabCategory.FOLDERS).get()
                folders.map { it.toTabDisplayableItem(resources, requestedSpanSize) }
            }
    }

    private fun getGenres(): Flow<List<DisplayableItem>> {
        return genreGateway.observeAll()
            .map { genres ->
                val requestedSpanSize = libraryPrefs.spanCount(TabCategory.GENRES).get()
                genres.map { it.toTabDisplayableItem(resources, requestedSpanSize) }
            }
    }

    private fun getPlaylist(): Flow<List<DisplayableItem>> {
        val autoPlaylist = playlistGateway.getAllAutoPlaylists()
            .map { it.toAutoPlaylist() }
            .startWith(headers.autoPlaylistHeader)

        return playlistGateway.observeAll().map { list ->
            val requestedSpanSize = libraryPrefs.spanCount(TabCategory.PLAYLISTS).get()

            list.asSequence().map { it.toTabDisplayableItem(resources, requestedSpanSize) }
                .toMutableList()
                .startWithIfNotEmpty(headers.allPlaylistHeader)
                .startWith(autoPlaylist)
        }
    }

    private fun getAlbums(): Flow<List<DisplayableItem>> {
        val recentlyAddedFlow = albumGateway.observeRecentlyAdded()
            .combine(libraryPrefs.newItemsVisibility.observe()) { data, canShow ->
                if (canShow) data else emptyList()
            }
        val recentlyPlayedFlow = albumGateway.observeLastPlayed()
            .combine(libraryPrefs.recentPlayedVisibility.observe()) { data, canShow ->
                if (canShow) data else emptyList()
            }

        return combine(
            albumGateway.observeAll()
                .map { albums ->
                    val requestedSpanSize = libraryPrefs.spanCount(TabCategory.ALBUMS).get()
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
            .combine(libraryPrefs.newItemsVisibility.observe()) { data, canShow ->
                if (canShow) data else emptyList()
            }
        val recentlyPlayedFlow = artistGateway.observeLastPlayed()
            .combine(libraryPrefs.recentPlayedVisibility.observe()) { data, canShow ->
                if (canShow) data else emptyList()
            }

        return combine(
            artistGateway.observeAll()
                .map { artists ->
                    val requestedSpanSize = libraryPrefs.spanCount(TabCategory.ARTISTS).get()
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

    // podcasts
    private fun getPodcastPlaylist(): Flow<List<DisplayableItem>> {
        val autoPlaylist = podcastPlaylistGateway.getAllAutoPlaylists()
            .map { it.toAutoPlaylist() }
            .startWith(headers.autoPlaylistHeader)

        return podcastPlaylistGateway.observeAll().map { list ->
            val requestedSpanSize = libraryPrefs.spanCount(TabCategory.PODCASTS_PLAYLIST).get()
            list.asSequence().map { it.toTabDisplayableItem(resources, requestedSpanSize) }
                .toMutableList()
                .startWithIfNotEmpty(headers.allPlaylistHeader)
                .startWith(autoPlaylist)
        }
    }

    private fun getPodcastAlbums(): Flow<List<DisplayableItem>> {
        val recentlyAddedFlow = podcastAlbumGateway.observeRecentlyAdded()
            .combine(libraryPrefs.newItemsVisibility.observe()) { data, canShow ->
                if (canShow) data else emptyList()
            }
        val recentlyPlayedFlow = podcastAlbumGateway.observeLastPlayed()
            .combine(libraryPrefs.recentPlayedVisibility.observe()) { data, canShow ->
                if (canShow) data else emptyList()
            }

        return combine(
            podcastAlbumGateway.observeAll()
                .map { albums ->
                    val requestedSpanSize =
                        libraryPrefs.spanCount(TabCategory.PODCASTS_ALBUMS).get()
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
            .combine(libraryPrefs.newItemsVisibility.observe()) { data, canShow ->
                if (canShow) data else emptyList()
            }
        val recentlyPlayedFlow = podcastArtistGateway.observeLastPlayed()
            .combine(libraryPrefs.recentPlayedVisibility.observe()) { data, canShow ->
                if (canShow) data else emptyList()
            }

        return combine(
            podcastArtistGateway.observeAll()
                .map { artists ->
                    val requestedSpanSize =
                        libraryPrefs.spanCount(TabCategory.PODCASTS_ARTISTS).get()
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