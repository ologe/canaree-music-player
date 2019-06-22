package dev.olog.msc.presentation.library.tab

import android.content.Context
import dev.olog.core.dagger.ApplicationContext
import dev.olog.core.gateway.AlbumGateway2
import dev.olog.core.gateway.ArtistGateway2
import dev.olog.core.gateway.FolderGateway2
import dev.olog.core.gateway.SongGateway2
import dev.olog.msc.presentation.library.tab.mapper.toTabDisplayableItem
import dev.olog.presentation.model.DisplayableItem
import dev.olog.shared.mapListItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class TabDataProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val folderGateway: FolderGateway2,
    private val songGateway: SongGateway2,
    private val albumGateway: AlbumGateway2,
    private val artistGateway: ArtistGateway2
) {

    fun get(category: TabCategory): Flow<List<DisplayableItem>> = when (category) {
        TabCategory.FOLDERS -> folderGateway.observeAll().mapListItem { it.toTabDisplayableItem(context.resources) }
        TabCategory.ALBUMS -> albumGateway.observeAll().mapListItem { it.toTabDisplayableItem() }
        TabCategory.ARTISTS -> artistGateway.observeAll().mapListItem { it.toTabDisplayableItem(context.resources) }
        else -> songGateway.observeAll()
            .mapListItem { it.toTabDisplayableItem() }
    }

}