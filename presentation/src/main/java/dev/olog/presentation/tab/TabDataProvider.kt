package dev.olog.presentation.tab

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.entity.track.Playlist
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
import dev.olog.shared.mapListItem
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

    fun get(category: TabCategory): Flow<List<DisplayableItem>> = when (category) {
        // songs
        TabCategory.FOLDERS -> getFolders()
        TabCategory.PLAYLISTS -> getPlaylist()
        TabCategory.SONGS -> songGateway.observeAll().map {
            buildList {
                if (it.isNotEmpty()) {
                    add(headers.shuffleHeader)
                    addAll(it.map { it.toTabDisplayableItem() })
                }
            }
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
        TabCategory.PODCASTS -> podcastGateway.observeAll().mapListItem { it.toTabDisplayableItem() }
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
        return playlistGateway.observeAll().map { playlists ->
            val requestedSpanSize = presentationPrefs.getSpanCount(TabCategory.PLAYLISTS)

            buildList {
                // auto
                add(headers.autoPlaylistHeader)
                addAll(playlistGateway.getAllAutoPlaylists().map(Playlist::toAutoPlaylist))

                // playlists
                if (playlists.isNotEmpty()) {
                    add(headers.allPlaylistHeader)
                    addAll(playlists.map { it.toTabDisplayableItem(resources, requestedSpanSize) })
                }
            }
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
            buildList {
                if (recentlyAdded.isNotEmpty()) {
                    addAll(headers.recentlyAddedAlbumsHeaders)
                }
                if (lastPlayed.isNotEmpty()) {
                    addAll(headers.lastPlayedAlbumHeaders)
                }
                if (this.isNotEmpty() && all.isNotEmpty()) {
                    addAll(headers.allAlbumsHeader)
                }
                addAll(all)
            }
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
            buildList {
                if (recentlyAdded.isNotEmpty()) {
                    addAll(headers.recentlyAddedArtistsHeaders)
                }
                if (lastPlayed.isNotEmpty()) {
                    addAll(headers.lastPlayedArtistHeaders)
                }
                if (this.isNotEmpty() && all.isNotEmpty()) {
                    addAll(headers.allArtistsHeader)
                }
                addAll(all)
            }
        }
    }

    // podcasts
    private fun getPodcastPlaylist(): Flow<List<DisplayableItem>> {
        return podcastPlaylistGateway.observeAll().map { playlists ->
            val requestedSpanSize = presentationPrefs.getSpanCount(TabCategory.PODCASTS_PLAYLIST)

            buildList {
                add(headers.autoPlaylistHeader)
                addAll(podcastPlaylistGateway.getAllAutoPlaylists().map(Playlist::toAutoPlaylist))

                if (playlists.isNotEmpty()) {
                    add(headers.allPlaylistHeader)
                    addAll(playlists.map { it.toTabDisplayableItem(resources, requestedSpanSize) })
                }
            }
        }
    }

    private fun getPodcastAlbums(): Flow<List<DisplayableItem>> {
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
                    val requestedSpanSize =
                        presentationPrefs.getSpanCount(TabCategory.PODCASTS_ALBUMS)
                    albums.map { it.toTabDisplayableItem(requestedSpanSize) }
                },
            recentlyAddedFlow,
            recentlyPlayedFlow
        ) { all, recentlyAdded, lastPlayed ->
            buildList {
                if (recentlyAdded.isNotEmpty()) {
                    addAll(headers.recentlyAddedAlbumsHeaders)
                }
                if (lastPlayed.isNotEmpty()) {
                    addAll(headers.lastPlayedAlbumHeaders)
                }
                if (this.isNotEmpty() && all.isNotEmpty()) {
                    addAll(headers.allAlbumsHeader)
                }
                addAll(all)
            }
        }
    }

    private fun getPodcastArtists(): Flow<List<DisplayableItem>> {
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
                    val requestedSpanSize =
                        presentationPrefs.getSpanCount(TabCategory.PODCASTS_ARTISTS)
                    artists.map { it.toTabDisplayableItem(resources, requestedSpanSize) }
                },
            recentlyAddedFlow,
            recentlyPlayedFlow
        ) { all, recentlyAdded, lastPlayed ->
            buildList {
                if (recentlyAdded.isNotEmpty()) {
                    addAll(headers.recentlyAddedArtistsHeaders)
                }
                if (lastPlayed.isEmpty()) {
                    addAll(headers.lastPlayedArtistHeaders)
                }
                if (this.isNotEmpty() && all.isNotEmpty()) {
                    addAll(headers.allArtistsHeader)
                }
                addAll(all)
            }
        }
    }
}