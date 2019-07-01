package dev.olog.presentation.tab

import android.content.Context
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.gateway.*
import dev.olog.presentation.model.DisplayableItem
import dev.olog.presentation.tab.mapper.toTabDisplayableItem
import dev.olog.presentation.tab.mapper.toTabLastPlayedDisplayableItem
import dev.olog.shared.extensions.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
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
        private val podcastArtistGateway: PodcastArtistGateway
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
        return playlistGateway.observeAll().map { list ->
            list.asSequence().map { it.toTabDisplayableItem(resources) }
                .toMutableList()
                .startWithIfNotEmpty(headers.allPlaylistHeader)
        }.combineLatest(
            flowOf(playlistGateway.getAllAutoPlaylists().map { it.toTabDisplayableItem(resources) }.startWith(headers.autoPlaylistHeader))
        ) { all, auto ->
            auto + all
        }
    }

    private fun getAlbums(): Flow<List<DisplayableItem>> {
        return albumGateway.observeAll().mapListItem { it.toTabDisplayableItem() }
            .combineLatest(
                albumGateway.observeRecentlyAdded(),
                albumGateway.observeLastPlayed()
            ) { all, recentlyAdded, lastPlayed ->
                val result = mutableListOf<DisplayableItem>()
                result.doIf(recentlyAdded.count() > 0) { addAll(headers.recentlyAddedAlbumsHeaders) }
                    .doIf(lastPlayed.count() > 0) { addAll(headers.lastPlayedAlbumHeaders) }
                    .doIf(result.isNotEmpty()) { addAll(headers.allAlbumsHeader) }
                    .plus(all)
            }
    }

    private fun getArtists(): Flow<List<DisplayableItem>> {
        return artistGateway.observeAll().mapListItem { it.toTabDisplayableItem(resources) }
            .combineLatest(
                artistGateway.observeRecentlyAdded(),
                artistGateway.observeLastPlayed()
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
        return podcastPlaylistGateway.observeAll().map { list ->
            list.asSequence().map { it.toTabDisplayableItem(resources) }
                .toMutableList()
                .startWithIfNotEmpty(headers.allPlaylistHeader)
        }
    }

    private fun getPodcastAlbums(): Flow<List<DisplayableItem>> {
        return podcastAlbumGateway.observeAll().mapListItem { it.toTabDisplayableItem() }
            .combineLatest(
                podcastAlbumGateway.observeRecentlyAdded(),
                podcastAlbumGateway.observeLastPlayed()
            ) { all, recentlyAdded, lastPlayed ->
                val result = mutableListOf<DisplayableItem>()
                result.doIf(recentlyAdded.count() > 0) { addAll(headers.recentlyAddedAlbumsHeaders) }
                    .doIf(lastPlayed.count() > 0) { addAll(headers.lastPlayedAlbumHeaders) }
                    .doIf(result.isNotEmpty()) { addAll(headers.allAlbumsHeader) }
                    .plus(all)
            }
    }

    private fun getPodcastArtists(): Flow<List<DisplayableItem>> {
        return podcastArtistGateway.observeAll().mapListItem { it.toTabDisplayableItem(resources) }
            .combineLatest(
                podcastArtistGateway.observeRecentlyAdded(),
                podcastArtistGateway.observeLastPlayed()
            ) { all, recentlyAdded, lastPlayed ->
                val result = mutableListOf<DisplayableItem>()
                result.doIf(recentlyAdded.count() > 0) { addAll(headers.recentlyAddedArtistsHeaders) }
                    .doIf(lastPlayed.count() > 0) { addAll(headers.lastPlayedArtistHeaders) }
                    .doIf(result.isNotEmpty()) { addAll(headers.allArtistsHeader) }
                    .plus(all)
            }
    }
}