package dev.olog.msc.presentation.library.tab

import android.content.Context
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.gateway.*
import dev.olog.msc.presentation.library.tab.mapper.toTabDisplayableItem
import dev.olog.msc.presentation.library.tab.mapper.toTabLastPlayedDisplayableItem
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.doIf
import dev.olog.shared.mapListItem
import dev.olog.shared.startWithIfNotEmpty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

internal class TabDataProvider @Inject constructor(
    @ApplicationContext context: Context,
    private val headers: TabFragmentHeaders,
    private val folderGateway: FolderGateway2,
    private val playlistGateway: PlaylistGateway2,
    private val songGateway: SongGateway2,
    private val albumGateway: AlbumGateway2,
    private val artistGateway: ArtistGateway2,
    private val genreGateway: GenreGateway2
) {

    private val resources = context.resources

    fun get(category: TabCategory): Flow<List<DisplayableItem>> = when (category) {
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
        else -> songGateway.observeAll().mapListItem { it.toTabDisplayableItem() }
    }.flowOn(Dispatchers.Default)

    private fun getPlaylist(): Flow<List<DisplayableItem>> {
        return playlistGateway.observeAll().map { list ->
            list.asSequence().map { it.toTabDisplayableItem(resources) }
                .toMutableList()
                .startWithIfNotEmpty(headers.allPlaylistHeader)
        }.combineLatest(
            flowOf(playlistGateway.getAllAutoPlaylists().map { it.toTabDisplayableItem(resources) })
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
                    .doIf(result.isNotEmpty()) { addAll(headers.allAlbumsHeader) }
                    .plus(all)
            }
    }

}