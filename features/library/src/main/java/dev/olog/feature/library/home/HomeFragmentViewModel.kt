package dev.olog.feature.library.home

import androidx.lifecycle.ViewModel
import dev.olog.domain.entity.track.Album
import dev.olog.domain.gateway.track.AlbumGateway
import dev.olog.feature.library.R
import dev.olog.feature.library.tab.TabFragmentHeaders
import dev.olog.feature.presentation.base.model.DisplayableAlbum
import dev.olog.feature.presentation.base.model.DisplayableItem
import dev.olog.feature.presentation.base.model.presentationId
import dev.olog.shared.coroutines.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

internal class HomeFragmentViewModel @Inject constructor(
    private val albumGateway: AlbumGateway,
    private val headers: TabFragmentHeaders
): ViewModel() {

    val data: Flow<List<DisplayableItem>>
        get() {
            return albumGateway.observeRecentlyAdded()
                .combine(albumGateway.observeLastPlayed()) { recentlyAdded, lastPlayed ->
                    val result = mutableListOf<DisplayableItem>()
                    result.add(headers.homeHeader)
                    if (recentlyAdded.isNotEmpty()) {
                        result.addAll(headers.recentlyAddedAlbumsHeaders)
                    }
                    if (lastPlayed.isNotEmpty()) {
                        result.addAll(headers.lastPlayedAlbumHeaders)
                    }
                    result
            }
        }

    val recentlyAdded: Flow<List<DisplayableAlbum>>
        get() = albumGateway.observeRecentlyAdded()
            .mapListItem { it.toTabLastPlayedDisplayableItem() }

    val lastPlayed: Flow<List<DisplayableAlbum>>
        get() = albumGateway.observeLastPlayed()
            .mapListItem { it.toTabLastPlayedDisplayableItem() }

    private fun Album.toTabLastPlayedDisplayableItem(): DisplayableAlbum {
        return DisplayableAlbum(
            type = R.layout.item_tab_album_last_played,
            mediaId = presentationId,
            title = title,
            subtitle = artist
        )
    }

}