package dev.olog.feature.library.tab.model

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.core.entity.track.Playlist
import dev.olog.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.core.gateway.podcast.PodcastArtistGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.*
import dev.olog.feature.library.prefs.LibraryPreferencesGateway
import dev.olog.feature.library.tab.TabFragmentHeaders
import dev.olog.shared.mapListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class TabFragmentDataProvider @Inject constructor(
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
    private val presentationPrefs: LibraryPreferencesGateway
) {

    private val resources = context.resources

    fun get(category: TabFragmentCategory): Flow<List<TabFragmentModel>> = when (category) {
        // songs
        TabFragmentCategory.FOLDERS -> getFolders()
        TabFragmentCategory.PLAYLISTS -> getPlaylist()
        TabFragmentCategory.SONGS -> songGateway.observeAll().map {
            buildList {
                if (it.isNotEmpty()) {
                    add(headers.shuffleHeader)
                    addAll(it.map { it.toTabPresentation() })
                }
            }
        }
        TabFragmentCategory.ALBUMS -> getAlbums()
        TabFragmentCategory.ARTISTS -> getArtists()
        TabFragmentCategory.GENRES -> getGenres()
        TabFragmentCategory.RECENTLY_ADDED_ALBUMS -> albumGateway.observeRecentlyAdded().mapListItem { it.toRecentlyPlayedDisplayableItem() }
        TabFragmentCategory.RECENTLY_ADDED_ARTISTS -> artistGateway.observeRecentlyAdded().mapListItem {
            it.toRecentlyPlayedDisplayableItem(resources)
        }
        TabFragmentCategory.LAST_PLAYED_ALBUMS -> albumGateway.observeLastPlayed().mapListItem { it.toRecentlyPlayedDisplayableItem() }
        TabFragmentCategory.LAST_PLAYED_ARTISTS -> artistGateway.observeLastPlayed().mapListItem {
            it.toRecentlyPlayedDisplayableItem(resources)
        }
        // podcasts
        TabFragmentCategory.PODCASTS_PLAYLIST -> getPodcastPlaylist()
        TabFragmentCategory.PODCASTS -> podcastGateway.observeAll().mapListItem { it.toTabPresentation() }
        TabFragmentCategory.PODCASTS_ALBUMS -> getPodcastAlbums()
        TabFragmentCategory.PODCASTS_ARTISTS -> getPodcastArtists()
        TabFragmentCategory.RECENTLY_ADDED_PODCAST_ALBUMS -> podcastAlbumGateway.observeRecentlyAdded().mapListItem { it.toRecentlyPlayedDisplayableItem() }
        TabFragmentCategory.RECENTLY_ADDED_PODCAST_ARTISTS -> podcastArtistGateway.observeRecentlyAdded().mapListItem {
            it.toRecentlyPlayedDisplayableItem(resources)
        }
        TabFragmentCategory.LAST_PLAYED_PODCAST_ALBUMS -> podcastAlbumGateway.observeLastPlayed().mapListItem { it.toRecentlyPlayedDisplayableItem() }
        TabFragmentCategory.LAST_PLAYED_PODCAST_ARTISTS -> podcastArtistGateway.observeLastPlayed().mapListItem {
            it.toRecentlyPlayedDisplayableItem(resources)
        }
    }.flowOn(Dispatchers.Default)

    private fun getFolders(): Flow<List<TabFragmentModel>> {
        return folderGateway.observeAll()
            .map { folders ->
                val requestedSpanSize = presentationPrefs.getSpanCount(TabFragmentCategory.FOLDERS)
                folders.map { it.toTabPresentation(resources, requestedSpanSize) }
            }
    }

    private fun getGenres(): Flow<List<TabFragmentModel>> {
        return genreGateway.observeAll()
            .map { genres ->
                val requestedSpanSize = presentationPrefs.getSpanCount(TabFragmentCategory.GENRES)
                genres.map { it.toTabPresentation(resources, requestedSpanSize) }
            }
    }

    private fun getPlaylist(): Flow<List<TabFragmentModel>> {
        return playlistGateway.observeAll().map { playlists ->
            val requestedSpanSize = presentationPrefs.getSpanCount(TabFragmentCategory.PLAYLISTS)

            buildList {
                // auto
                add(headers.autoPlaylistHeader)
                addAll(playlistGateway.getAllAutoPlaylists().map(Playlist::toTabAutoPlaylist))

                // playlists
                if (playlists.isNotEmpty()) {
                    add(headers.allPlaylistHeader)
                    addAll(playlists.map { it.toTabPresentation(resources, requestedSpanSize) })
                }
            }
        }
    }

    private fun getAlbums(): Flow<List<TabFragmentModel>> {
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
                    val requestedSpanSize = presentationPrefs.getSpanCount(TabFragmentCategory.ALBUMS)
                    albums.map { it.toTabPresentation(requestedSpanSize) }
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
                    add(headers.allAlbumsHeader)
                }
                addAll(all)
            }
        }
    }

    private fun getArtists(): Flow<List<TabFragmentModel>> {
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
                    val requestedSpanSize = presentationPrefs.getSpanCount(TabFragmentCategory.ARTISTS)
                    artists.map { it.toTabPresentation(resources, requestedSpanSize) }
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
                    add(headers.allArtistsHeader)
                }
                addAll(all)
            }
        }
    }

    // podcasts
    private fun getPodcastPlaylist(): Flow<List<TabFragmentModel>> {
        return podcastPlaylistGateway.observeAll().map { playlists ->
            val requestedSpanSize = presentationPrefs.getSpanCount(TabFragmentCategory.PODCASTS_PLAYLIST)

            buildList {
                add(headers.autoPlaylistHeader)
                addAll(podcastPlaylistGateway.getAllAutoPlaylists().map(Playlist::toTabAutoPlaylist))

                if (playlists.isNotEmpty()) {
                    add(headers.allPlaylistHeader)
                    addAll(playlists.map { it.toTabPresentation(resources, requestedSpanSize) })
                }
            }
        }
    }

    private fun getPodcastAlbums(): Flow<List<TabFragmentModel>> {
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
                        presentationPrefs.getSpanCount(TabFragmentCategory.PODCASTS_ALBUMS)
                    albums.map { it.toTabPresentation(requestedSpanSize) }
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
                    add(headers.allAlbumsHeader)
                }
                addAll(all)
            }
        }
    }

    private fun getPodcastArtists(): Flow<List<TabFragmentModel>> {
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
                        presentationPrefs.getSpanCount(TabFragmentCategory.PODCASTS_ARTISTS)
                    artists.map { it.toTabPresentation(resources, requestedSpanSize) }
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
                    add(headers.allArtistsHeader)
                }
                addAll(all)
            }
        }
    }
}