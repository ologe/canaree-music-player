package dev.olog.msc.presentation.library.tab

import android.content.Context
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.gateway.*
import dev.olog.msc.presentation.library.tab.mapper.toTabDisplayableItem
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.mapListItem
import dev.olog.shared.startWithIfNotEmpty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combineLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
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
        TabCategory.ALBUMS -> albumGateway.observeAll().mapListItem { it.toTabDisplayableItem() }
        TabCategory.ARTISTS -> artistGateway.observeAll().mapListItem { it.toTabDisplayableItem(resources) }
        TabCategory.GENRES -> genreGateway.observeAll().mapListItem { it.toTabDisplayableItem(resources) }
        else -> songGateway.observeAll().mapListItem { it.toTabDisplayableItem() }
    }

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

}