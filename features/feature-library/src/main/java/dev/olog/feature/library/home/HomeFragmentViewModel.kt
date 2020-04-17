package dev.olog.feature.library.home

import android.content.Context
import androidx.lifecycle.ViewModel
import dev.olog.domain.entity.track.Album
import dev.olog.domain.entity.track.GeneratedPlaylist
import dev.olog.domain.gateway.spotify.SpotifyGateway
import dev.olog.domain.gateway.track.AlbumGateway
import dev.olog.domain.interactor.SpotifyFetcherUseCase
import dev.olog.feature.library.R
import dev.olog.feature.presentation.base.model.*
import dev.olog.shared.coroutines.mapListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

internal class HomeFragmentViewModel @Inject constructor(
    private val context: Context,
    private val albumGateway: AlbumGateway,
    private val spotifyFetcherUseCase: SpotifyFetcherUseCase,
    private val spotifyGateway: SpotifyGateway
): ViewModel() {

    val data: Flow<List<DisplayableItem>>
        get() {
            return combine(
                albumGateway.observeRecentlyAdded(),
                albumGateway.observeLastPlayed(),
                spotifyGateway.observePlaylists()
            ) { recentlyAdded, lastPlayed, playlists ->
                val result = mutableListOf<DisplayableItem>()
                result.add(homeHeader)
                if (playlists.isNotEmpty()) {
                    result.addAll(generatedPlaylistHeaders)
                }
                if (recentlyAdded.isNotEmpty()) {
                    result.addAll(recentlyAddedAlbumsHeaders)
                }
                if (lastPlayed.isNotEmpty()) {
                    result.addAll(lastPlayedAlbumHeaders)
                }
                result
            }
        }

    val observeSpotifyFetchProgress: Flow<Int>
        get() = spotifyFetcherUseCase.observeStatus()

    val recentlyAdded: Flow<List<DisplayableAlbum>>
        get() = albumGateway.observeRecentlyAdded()
            .mapListItem { it.toLastPlayedDisplayableItem() }

    val generatedPlaylists: Flow<List<DisplayableAlbum>>
        get() = spotifyGateway.observePlaylists()
            .mapListItem { it.toDisplayableItem() }

    val lastPlayed: Flow<List<DisplayableAlbum>>
        get() = albumGateway.observeLastPlayed()
            .mapListItem { it.toLastPlayedDisplayableItem() }

    private val homeHeader = DisplayableHeader(
        R.layout.item_home,
        PresentationId.headerId("home header"),
        ""
    )

    private val lastPlayedAlbumHeaders = listOf(
        DisplayableHeader(
            R.layout.item_home_header,
            PresentationId.headerId("recently played albums"),
            context.getString(R.string.tab_recent_played)
        ),
        DisplayableNestedListPlaceholder(
            R.layout.item_home_last_played_horizontal_list,
            PresentationId.headerId("recently played albums list")
        )
    )

    private val recentlyAddedAlbumsHeaders = listOf(
        DisplayableHeader(
            R.layout.item_home_header, PresentationId.headerId("recently added albums"),
            context.getString(R.string.tab_recent_added)
        ),
        DisplayableNestedListPlaceholder(
            R.layout.item_home_new_album_horizontal_list,
            PresentationId.headerId("recently added albums list")
        )
    )

    private val generatedPlaylistHeaders = listOf(
        DisplayableHeader(
            R.layout.item_home_header, PresentationId.headerId("generated playlists"),
            "Uniquely yours" // TODO localization
        ),
        DisplayableNestedListPlaceholder(
            R.layout.item_home_generated_playlists_horizontal_list,
            PresentationId.headerId("generated playlists list")
        )
    )

    private fun Album.toLastPlayedDisplayableItem(): DisplayableAlbum {
        return DisplayableAlbum(
            type = R.layout.item_home_last_played,
            mediaId = presentationId,
            title = title,
            subtitle = artist
        )
    }

    private fun GeneratedPlaylist.toDisplayableItem(): DisplayableAlbum {
        return DisplayableAlbum(
            type = R.layout.item_home_generated_playlist,
            mediaId = presentationId,
            title = title,
            subtitle = DisplayableAlbum.readableSongCount(context.resources, size)
        )
    }

}