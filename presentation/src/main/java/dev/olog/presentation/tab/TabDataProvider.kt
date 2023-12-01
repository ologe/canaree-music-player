package dev.olog.presentation.tab

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.core.gateway.podcast.PodcastArtistGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.*
import dev.olog.presentation.model.PresentationPreferencesGateway
import dev.olog.presentation.tab.adapter.TabFragmentItem
import dev.olog.presentation.tab.mapper.toAutoPlaylist
import dev.olog.presentation.tab.mapper.toTabDisplayableItem
import dev.olog.presentation.tab.mapper.toTabLastPlayedDisplayableItem
import dev.olog.shared.mapListItem
import dev.olog.shared.startWith
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
    private val podcastPlaylistGateway: PodcastPlaylistGateway,
    private val podcastGateway: PodcastGateway,
    private val podcastAlbumGateway: PodcastAlbumGateway,
    private val podcastArtistGateway: PodcastArtistGateway,
    private val presentationPrefs: PresentationPreferencesGateway
) {

    private val resources = context.resources

    fun get(category: TabCategory, spanCount: Int): Flow<List<TabFragmentItem>> = when (category) {
        // songs
        TabCategory.FOLDERS -> getFolders(spanCount)
        TabCategory.PLAYLISTS -> getPlaylist(spanCount)
        TabCategory.SONGS -> songGateway.observeAll().map {
            it.map { it.toTabDisplayableItem() }.startWithIfNotEmpty(TabFragmentItem.Shuffle)
        }
        TabCategory.ALBUMS -> getAlbums(spanCount)
        TabCategory.ARTISTS -> getArtists(spanCount)
        TabCategory.GENRES -> getGenres(spanCount)
        // podcasts
        TabCategory.PODCASTS_PLAYLIST -> getPodcastPlaylist(spanCount)
        TabCategory.PODCASTS -> podcastGateway.observeAll().map {
            it.map { it.toTabDisplayableItem() }.startWithIfNotEmpty(TabFragmentItem.Shuffle)
        }
        TabCategory.PODCASTS_ALBUMS -> getPodcastAlbums(spanCount)
        TabCategory.PODCASTS_ARTISTS -> getPodcastArtists(spanCount)
    }.flowOn(Dispatchers.Default)

    private fun getFolders(spanCount: Int): Flow<List<TabFragmentItem>> {
        return folderGateway.observeAll()
            .map { folders ->
                folders.map { it.toTabDisplayableItem(resources, spanCount) }
            }
    }

    private fun getGenres(spanCount: Int): Flow<List<TabFragmentItem>> {
        return genreGateway.observeAll()
            .map { genres ->
                genres.map { it.toTabDisplayableItem(resources, spanCount) }
            }
    }

    private fun getPlaylist(spanCount: Int): Flow<List<TabFragmentItem>> {
        val autoPlaylist = playlistGateway.getAllAutoPlaylists()
            .map { it.toAutoPlaylist() }
            .startWith(headers.autoPlaylistHeader)

        return playlistGateway.observeAll().map { list ->
            list.asSequence().map { it.toTabDisplayableItem(resources, spanCount) }
                .toMutableList()
                .startWithIfNotEmpty(headers.allPlaylistHeader)
                .startWith(autoPlaylist)
        }
    }

    private fun getAlbums(spanCount: Int): Flow<List<TabFragmentItem>> {
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
                    albums.map { it.toTabDisplayableItem(spanCount) }
                },
            recentlyAddedFlow.mapListItem { it.toTabLastPlayedDisplayableItem() },
            recentlyPlayedFlow.mapListItem { it.toTabLastPlayedDisplayableItem() }
        ) { all, recentlyAdded, recentlyPlayed ->
            buildList {
                if (recentlyAdded.isNotEmpty()) {
                    addAll(headers.recentlyAddedAlbums(recentlyAdded))
                }
                if (recentlyPlayed.isNotEmpty()) {
                    addAll(headers.recentlyPlayed(recentlyPlayed))
                }
                if (this.isNotEmpty()) {
                    add(headers.allArtistsHeader)
                }
                addAll(all)
            }
        }
    }

    private fun getArtists(spanCount: Int): Flow<List<TabFragmentItem>> {
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
                    artists.map { it.toTabDisplayableItem(resources, spanCount) }
                },
            recentlyAddedFlow.mapListItem { it.toTabLastPlayedDisplayableItem(resources) },
            recentlyPlayedFlow.mapListItem { it.toTabLastPlayedDisplayableItem(resources) }
        ) { all, recentlyAdded, recentlyPlayed ->
            buildList {
                if (recentlyAdded.isNotEmpty()) {
                    addAll(headers.recentlyAddedArtist(recentlyAdded))
                }
                if (recentlyPlayed.isNotEmpty()) {
                    addAll(headers.recentlyPlayed(recentlyPlayed))
                }
                if (this.isNotEmpty()) {
                    add(headers.allArtistsHeader)
                }
                addAll(all)
            }
        }
    }

    // podcasts
    private fun getPodcastPlaylist(spanCount: Int): Flow<List<TabFragmentItem>> {
        val autoPlaylist = podcastPlaylistGateway.getAllAutoPlaylists()
            .map { it.toAutoPlaylist() }
            .startWith(headers.autoPlaylistHeader)

        return podcastPlaylistGateway.observeAll().map { list ->
            list.asSequence().map { it.toTabDisplayableItem(resources, spanCount) }
                .toMutableList()
                .startWithIfNotEmpty(headers.allPlaylistHeader)
                .startWith(autoPlaylist)
        }
    }

    private fun getPodcastAlbums(spanCount: Int): Flow<List<TabFragmentItem>> {
        val recentlyAddedFlow = podcastAlbumGateway.observeRecentlyAdded()
            .combine(presentationPrefs.observeLibraryNewVisibility()) { data, canShow ->
                if (canShow) data else emptyList()
            }
        val recentlyPlayedFlow = podcastAlbumGateway.observeLastPlayed()
            .combine(presentationPrefs.observeLibraryRecentPlayedVisibility()) { data, canShow ->
                if (canShow) data else emptyList()
            }

        return combine(
            podcastAlbumGateway.observeAll()
                .map { albums ->
                    albums.map { it.toTabDisplayableItem(spanCount) }
                },
            recentlyAddedFlow.mapListItem { it.toTabLastPlayedDisplayableItem() },
            recentlyPlayedFlow.mapListItem { it.toTabLastPlayedDisplayableItem() },
        ) { all, recentlyAdded, recentlyPlayed ->
            buildList {
                if (recentlyAdded.isNotEmpty()) {
                    addAll(headers.recentlyAddedAlbums(recentlyAdded))
                }
                if (recentlyPlayed.isNotEmpty()) {
                    addAll(headers.recentlyPlayed(recentlyPlayed))
                }
                if (this.isNotEmpty()) {
                    add(headers.allAlbumsHeader)
                }
                addAll(all)
            }
        }
    }

    private fun getPodcastArtists(spanCount: Int): Flow<List<TabFragmentItem>> {
        val recentlyAddedFlow = podcastArtistGateway.observeRecentlyAdded()
            .combine(presentationPrefs.observeLibraryNewVisibility()) { data, canShow ->
                if (canShow) data else emptyList()
            }
        val recentlyPlayedFlow = podcastArtistGateway.observeLastPlayed()
            .combine(presentationPrefs.observeLibraryRecentPlayedVisibility()) { data, canShow ->
                if (canShow) data else emptyList()
            }

        return combine(
            podcastArtistGateway.observeAll()
                .map { artists ->
                    artists.map { it.toTabDisplayableItem(resources, spanCount) }
                },
            recentlyAddedFlow.mapListItem { it.toTabLastPlayedDisplayableItem(resources) },
            recentlyPlayedFlow.mapListItem { it.toTabLastPlayedDisplayableItem(resources) }
        ) { all, recentlyAdded, recentlyPlayed ->
            buildList {
                if (recentlyAdded.isNotEmpty()) {
                    addAll(headers.recentlyAddedArtist(recentlyAdded))
                }
                if (recentlyPlayed.isNotEmpty()) {
                    addAll(headers.recentlyPlayed(recentlyPlayed))
                }
                if (this.isNotEmpty()) {
                    add(headers.allArtistsHeader)
                }
                addAll(all)
            }
        }
    }
}