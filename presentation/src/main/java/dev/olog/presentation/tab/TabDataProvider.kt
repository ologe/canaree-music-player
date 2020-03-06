package dev.olog.presentation.tab

import android.content.Context
import dev.olog.core.gateway.podcast.PodcastAuthorGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.*
import dev.olog.core.schedulers.Schedulers
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.presentation.tab.mapper.toAutoPlaylist
import dev.olog.presentation.tab.mapper.toTabDisplayableItem
import dev.olog.presentation.tab.mapper.toTabLastPlayedDisplayableItem
import dev.olog.shared.*
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
    private val podcastPlaylistGateway: PodcastPlaylistGateway,
    private val podcastGateway: PodcastGateway,
    private val podcastAuthorGateway: PodcastAuthorGateway,
    private val presentationPrefs: PresentationPreferencesGateway,
    private val schedulers: Schedulers
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
            it.map { it.toTabDisplayableItem() }
        }
        TabCategory.PODCASTS_AUTHORS -> getPodcastArtists()
        TabCategory.RECENTLY_ADDED_PODCAST_ARTISTS -> podcastAuthorGateway.observeRecentlyAdded().mapListItem {
            it.toTabLastPlayedDisplayableItem(
                resources
            )
        }
        TabCategory.LAST_PLAYED_PODCAST_ARTISTS -> podcastAuthorGateway.observeLastPlayed().mapListItem {
            it.toTabLastPlayedDisplayableItem(
                resources
            )
        }
    }.flowOn(schedulers.cpu)

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

    private fun getPlaylist(): Flow<List<DisplayableItem>> {
        val autoPlaylist = playlistGateway.getAllAutoPlaylists()
            .map { it.toAutoPlaylist() }
            .startWith(headers.autoPlaylistHeader)

        return playlistGateway.observeAll().map { list ->
            val requestedSpanSize = presentationPrefs.getSpanCount(TabCategory.PLAYLISTS)

            list.asSequence().map { it.toTabDisplayableItem(resources, requestedSpanSize) }
                .toMutableList()
                .startWithIfNotEmpty(headers.allPlaylistHeader)
                .startWith(autoPlaylist)
        }
    }

    private fun getAlbums(): Flow<List<DisplayableItem>> {
        val recentlyAddedFlow = albumGateway.observeRecentlyAdded()
            .combine(presentationPrefs.observeLibraryNewVisibility()) { data, canShow ->
                if (canShow) data else emptyList()
            }
        val recentlyPlayedFlow = albumGateway.observeLastPlayed()
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
        val recentlyPlayedFlow = artistGateway.observeLastPlayed()
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

    // podcasts
    private fun getPodcastPlaylist(): Flow<List<DisplayableItem>> {
        val autoPlaylist = podcastPlaylistGateway.getAllAutoPlaylists()
            .map { it.toAutoPlaylist() }
            .startWith(headers.autoPlaylistHeader)

        return podcastPlaylistGateway.observeAll().map { list ->
            val requestedSpanSize = presentationPrefs.getSpanCount(TabCategory.PODCASTS_PLAYLIST)
            list.asSequence().map { it.toTabDisplayableItem(resources, requestedSpanSize) }
                .toMutableList()
                .startWithIfNotEmpty(headers.allPlaylistHeader)
                .startWith(autoPlaylist)
        }
    }

    private fun getPodcastArtists(): Flow<List<DisplayableItem>> {
        val recentlyAddedFlow = podcastAuthorGateway.observeRecentlyAdded()
            .combine(presentationPrefs.observeLibraryNewVisibility()) { data, canShow ->
                if (canShow) data else emptyList()
            }
        val recentlyPlayedFlow = podcastAuthorGateway.observeLastPlayed()
            .combine(presentationPrefs.observeLibraryRecentPlayedVisibility()) { data, canShow ->
                if (canShow) data else emptyList()
            }

        return combine(
            podcastAuthorGateway.observeAll()
                .map { artists ->
                    val requestedSpanSize =
                        presentationPrefs.getSpanCount(TabCategory.PODCASTS_AUTHORS)
                    artists.map { it.toTabDisplayableItem(resources, requestedSpanSize) }
                },
            recentlyAddedFlow,
            recentlyPlayedFlow
        ) { all, recentlyAdded, lastPlayed ->
            val result = mutableListOf<DisplayableItem>()
            result.doIf(recentlyAdded.count() > 0) { addAll(headers.recentlyAddedPodcastAuthorsHeaders) }
                .doIf(lastPlayed.count() > 0) { addAll(headers.lastPlayedPodcastAuthorsHeaders) }
                .doIf(result.isNotEmpty()) { addAll(headers.allPodcastAuthorsHeader) }
                .plus(all)
        }
    }
}