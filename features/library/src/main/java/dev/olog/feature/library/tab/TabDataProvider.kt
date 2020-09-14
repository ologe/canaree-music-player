package dev.olog.feature.library.tab

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.shared.coroutines.mapListItem
import dev.olog.domain.gateway.podcast.PodcastAuthorGateway
import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.domain.gateway.track.*
import dev.olog.domain.schedulers.Schedulers
import dev.olog.feature.library.model.TabCategory
import dev.olog.feature.library.prefs.LibraryPreferences
import dev.olog.feature.presentation.base.model.DisplayableItem
import dev.olog.shared.doIf
import dev.olog.shared.startWith
import dev.olog.shared.startWithIfNotEmpty
import kotlinx.coroutines.flow.*
import javax.inject.Inject

internal class TabDataProvider @Inject constructor(
    @ApplicationContext context: Context,
    private val headers: TabFragmentHeaders,
    // songs
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val genreGateway: GenreGateway,
    // podcast
    private val podcastPlaylistGateway: PodcastPlaylistGateway,
    private val preferences: LibraryPreferences,
    private val schedulers: Schedulers
) {

    private val resources = context.resources

    fun get(category: TabCategory): Flow<List<DisplayableItem>> = when (category) {
        // songs
        TabCategory.FOLDERS -> getFolders()
        TabCategory.PLAYLISTS -> getPlaylist()
        TabCategory.SONGS -> emptyFlow()
        TabCategory.ALBUMS -> emptyFlow()
        TabCategory.ARTISTS -> emptyFlow()
        TabCategory.GENRES -> getGenres()
        TabCategory.RECENTLY_ADDED_ALBUMS -> emptyFlow()
        TabCategory.RECENTLY_ADDED_ARTISTS -> emptyFlow()
        TabCategory.LAST_PLAYED_ALBUMS -> emptyFlow()
        TabCategory.LAST_PLAYED_ARTISTS -> emptyFlow()
        // podcasts
        TabCategory.PODCASTS_PLAYLIST -> getPodcastPlaylist()
        TabCategory.PODCASTS -> emptyFlow()
        TabCategory.PODCASTS_AUTHORS -> emptyFlow()
        TabCategory.RECENTLY_ADDED_PODCAST_ARTISTS -> emptyFlow()
        TabCategory.LAST_PLAYED_PODCAST_ARTISTS -> emptyFlow()
    }.flowOn(schedulers.cpu)

    private fun getFolders(): Flow<List<DisplayableItem>> {
        return folderGateway.observeAll()
            .map { folders ->
                val requestedSpanSize = preferences.getSpanCount(TabCategory.FOLDERS)
                folders.map { it.toTabDisplayableItem(resources, requestedSpanSize) }
            }
    }

    private fun getGenres(): Flow<List<DisplayableItem>> {
        return genreGateway.observeAll()
            .map { genres ->
                val requestedSpanSize = preferences.getSpanCount(TabCategory.GENRES)
                genres.map { it.toTabDisplayableItem(resources, requestedSpanSize) }
            }
    }

    private fun getPlaylist(): Flow<List<DisplayableItem>> {
        val autoPlaylist = playlistGateway.getAllAutoPlaylists()
            .map { it.toAutoPlaylist() }
            .startWith(headers.autoPlaylistHeader)

        return playlistGateway.observeAll().map { list ->
            val requestedSpanSize = preferences.getSpanCount(TabCategory.PLAYLISTS)

            list.asSequence().map { it.toTabDisplayableItem(resources, requestedSpanSize) }
                .toMutableList()
                .startWithIfNotEmpty(headers.allPlaylistHeader)
                .startWith(autoPlaylist)
        }
    }

    // podcasts
    private fun getPodcastPlaylist(): Flow<List<DisplayableItem>> {
        val autoPlaylist = podcastPlaylistGateway.getAllAutoPlaylists()
            .map { it.toAutoPlaylist() }
            .startWith(headers.autoPlaylistHeader)

        return podcastPlaylistGateway.observeAll().map { list ->
            val requestedSpanSize = preferences.getSpanCount(TabCategory.PODCASTS_PLAYLIST)
            list.asSequence().map { it.toTabDisplayableItem(resources, requestedSpanSize) }
                .toMutableList()
                .startWithIfNotEmpty(headers.allPlaylistHeader)
                .startWith(autoPlaylist)
        }
    }

}