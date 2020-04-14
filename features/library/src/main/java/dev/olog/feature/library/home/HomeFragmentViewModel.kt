package dev.olog.feature.library.home

import android.content.Context
import androidx.lifecycle.ViewModel
import dev.olog.domain.entity.track.Album
import dev.olog.domain.gateway.track.AlbumGateway
import dev.olog.feature.library.R
import dev.olog.feature.presentation.base.model.*
import dev.olog.shared.coroutines.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

internal class HomeFragmentViewModel @Inject constructor(
    context: Context,
    private val albumGateway: AlbumGateway
): ViewModel() {

    val data: Flow<List<DisplayableItem>>
        get() {
            return albumGateway.observeRecentlyAdded()
                .combine(albumGateway.observeLastPlayed()) { recentlyAdded, lastPlayed ->
                    val result = mutableListOf<DisplayableItem>()
                    result.add(homeHeader)
                    if (recentlyAdded.isNotEmpty()) {
                        result.addAll(recentlyAddedAlbumsHeaders)
                    }
                    if (lastPlayed.isNotEmpty()) {
                        result.addAll(lastPlayedAlbumHeaders)
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

    private val homeHeader = DisplayableHeader(
        R.layout.item_home_header,
        PresentationId.headerId("home header"),
        ""
    )

    private val lastPlayedAlbumHeaders = listOf(
        DisplayableHeader(
            R.layout.item_tab_header,
            PresentationId.headerId("recently played albums"),
            context.getString(R.string.tab_recent_played)
        ),
        DisplayableNestedListPlaceholder(
            R.layout.item_tab_last_played_album_horizontal_list,
            PresentationId.headerId("recently played albums list")
        )
    )

    private val recentlyAddedAlbumsHeaders = listOf(
        DisplayableHeader(
            R.layout.item_tab_header, PresentationId.headerId("recently added albums"),
            context.getString(R.string.tab_recent_added)
        ),
        DisplayableNestedListPlaceholder(
            R.layout.item_tab_new_album_horizontal_list,
            PresentationId.headerId("recently added albums list")
        )
    )

    private fun Album.toTabLastPlayedDisplayableItem(): DisplayableAlbum {
        return DisplayableAlbum(
            type = R.layout.item_tab_album_last_played,
            mediaId = presentationId,
            title = title,
            subtitle = artist
        )
    }

}