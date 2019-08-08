package dev.olog.presentation.tab

import android.content.Context
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.core.gateway.podcast.PodcastArtistGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.*
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.presentation.tab.mapper.toAutoPlaylist
import dev.olog.presentation.tab.mapper.toTabDisplayableItem
import dev.olog.presentation.tab.mapper.toTabLastPlayedDisplayableItem
import dev.olog.shared.doIf
import dev.olog.shared.mapListItem
import dev.olog.shared.startWith
import dev.olog.shared.startWithIfNotEmpty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combineLatest
import kotlinx.coroutines.flow.flowOf
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
    private val podcastAlbumGateway: PodcastAlbumGateway,
    private val podcastArtistGateway: PodcastArtistGateway,
    private val presentationPrefs: PresentationPreferencesGateway
) {

    private val resources = context.resources

    fun get(category: TabCategory): Flow<List<DisplayableItem>> = when (category) {
        // songs
        TabCategory.FOLDERS -> folderGateway.observeAll().mapListItem { it.toTabDisplayableItem(resources) }
        TabCategory.PLAYLISTS -> getPlaylist()
        TabCategory.SONGS -> songGateway.observeAll().map {
            it.map { it.toTabDisplayableItem() }.startWithIfNotEmpty(headers.shuffleHeader)
        }
        TabCategory.ALBUMS -> getAlbums()
        TabCategory.ARTISTS -> getArtists()
        TabCategory.GENRES -> genreGateway.observeAll().mapListItem { it.toTabDisplayableItem(resources) }
        TabCategory.RECENTLY_ADDED_ALBUMS -> albumGateway.observeRecentlyAdded().mapListItem { it.toTabLastPlayedDisplayableItem() }
        TabCategory.RECENTLY_ADDED_ARTISTS -> artistGateway.observeRecentlyAdded().mapListItem { it.toTabLastPlayedDisplayableItem(resources) }
        TabCategory.LAST_PLAYED_ALBUMS -> albumGateway.observeLastPlayed().mapListItem { it.toTabLastPlayedDisplayableItem() }
        TabCategory.LAST_PLAYED_ARTISTS -> artistGateway.observeLastPlayed().mapListItem { it.toTabLastPlayedDisplayableItem(resources) }
        // podcasts
        TabCategory.PODCASTS_PLAYLIST -> getPodcastPlaylist()
        TabCategory.PODCASTS -> podcastGateway.observeAll().map {
            it.map { it.toTabDisplayableItem() }.startWithIfNotEmpty(headers.shuffleHeader)
        }
        TabCategory.PODCASTS_ALBUMS -> getPodcastAlbums()
        TabCategory.PODCASTS_ARTISTS -> getPodcastArtists()
        TabCategory.RECENTLY_ADDED_PODCAST_ALBUMS -> podcastAlbumGateway.observeRecentlyAdded().mapListItem { it.toTabLastPlayedDisplayableItem() }
        TabCategory.RECENTLY_ADDED_PODCAST_ARTISTS -> podcastArtistGateway.observeRecentlyAdded().mapListItem { it.toTabLastPlayedDisplayableItem(resources) }
        TabCategory.LAST_PLAYED_PODCAST_ALBUMS -> podcastAlbumGateway.observeLastPlayed().mapListItem { it.toTabLastPlayedDisplayableItem() }
        TabCategory.LAST_PLAYED_PODCAST_ARTISTS -> podcastArtistGateway.observeLastPlayed().mapListItem { it.toTabLastPlayedDisplayableItem(resources) }
    }.flowOn(Dispatchers.Default)

    // songs

    private fun getPlaylist(): Flow<List<DisplayableItem>> {
        val autoPlaylist = playlistGateway.getAllAutoPlaylists()
            .map { it.toAutoPlaylist() }
            .startWith(headers.autoPlaylistHeader)

        return playlistGateway.observeAll().map { list ->
            list.asSequence().map { it.toTabDisplayableItem(resources) }
                .toMutableList()
                .startWithIfNotEmpty(headers.allPlaylistHeader)
                .startWith(autoPlaylist)
        }
    }

    private fun getAlbums(): Flow<List<DisplayableItem>> {
        val recentlyAddedFlow = albumGateway.observeRecentlyAdded()
            .combineLatest(presentationPrefs.observeLibraryNewVisibility()) { data, canShow ->
                if (canShow) data else emptyList()
            }
        val recentlyPlayedFlow = albumGateway.observeLastPlayed()
            .combineLatest(presentationPrefs.observeLibraryRecentPlayedVisibility()) { data, canShow ->
                if (canShow) data else emptyList()
            }

        return albumGateway.observeAll().mapListItem { it.toTabDisplayableItem() }
            .combineLatest(
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
            .combineLatest(presentationPrefs.observeLibraryNewVisibility()) { data, canShow ->
                if (canShow) data else emptyList()
            }
        val recentlyPlayedFlow = artistGateway.observeLastPlayed()
            .combineLatest(presentationPrefs.observeLibraryRecentPlayedVisibility()) { data, canShow ->
                if (canShow) data else emptyList()
            }

        return artistGateway.observeAll().mapListItem { it.toTabDisplayableItem(resources) }
            .combineLatest(
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
            list.asSequence().map { it.toTabDisplayableItem(resources) }
                .toMutableList()
                .startWithIfNotEmpty(headers.allPlaylistHeader)
                .startWith(autoPlaylist)
        }
    }

    private fun getPodcastAlbums(): Flow<List<DisplayableItem>> {
        val recentlyAddedFlow = podcastAlbumGateway.observeRecentlyAdded()
            .combineLatest(presentationPrefs.observeLibraryNewVisibility()) { data, canShow ->
                if (canShow) data else emptyList()
            }
        val recentlyPlayedFlow = podcastAlbumGateway.observeLastPlayed()
            .combineLatest(presentationPrefs.observeLibraryRecentPlayedVisibility()) { data, canShow ->
                if (canShow) data else emptyList()
            }

        return podcastAlbumGateway.observeAll().mapListItem { it.toTabDisplayableItem() }
            .combineLatest(
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
            .combineLatest(presentationPrefs.observeLibraryNewVisibility()) { data, canShow ->
                if (canShow) data else emptyList()
            }
        val recentlyPlayedFlow = podcastArtistGateway.observeLastPlayed()
            .combineLatest(presentationPrefs.observeLibraryRecentPlayedVisibility()) { data, canShow ->
                if (canShow) data else emptyList()
            }

        return podcastArtistGateway.observeAll().mapListItem { it.toTabDisplayableItem(resources) }
            .combineLatest(
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