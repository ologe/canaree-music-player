package dev.olog.feature.library.home

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.domain.entity.track.Album
import dev.olog.domain.entity.track.Artist
import dev.olog.domain.gateway.track.AlbumGateway
import dev.olog.domain.gateway.track.ArtistGateway
import dev.olog.feature.library.R
import dev.olog.feature.library.home.HomeFragmentModel.Header
import dev.olog.feature.library.home.HomeFragmentModel.Item
import dev.olog.feature.presentation.base.model.DisplayableAlbum
import dev.olog.feature.presentation.base.model.toPresentation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

// TODO think something for podcasts
// TODO rename LastPlayed to RecentlyPlayed everywhere
// TODO there is also already the implementation for recent podcast author
// TODO less played?
internal class HomeFragmentViewModel @ViewModelInject constructor(
    @ApplicationContext private val context: Context,
    private val albumGateway: AlbumGateway,
    private val artistGateway: ArtistGateway
) : ViewModel() {

    val data: Flow<List<HomeFragmentModel>>
        get() {
            return combine(
                albumGateway.observeLastPlayed().map { it.toRecentlyPlayed() },
                artistGateway.observeLastPlayed().map { it.toRecentlyPlayed() },
                albumGateway.observeRecentlyAdded().map { it.toRecentlyAdded() },
                artistGateway.observeRecentlyAdded().map { it.toRecentlyAdded() },
            ) { recentlyPlayedAlbums, recentlyPlayedArtists, recentlyAddedAlbums, recentlyAddedArtists ->
                buildList {
                    if (recentlyPlayedAlbums.isNotEmpty() || recentlyPlayedArtists.isNotEmpty()) {
                        add(Header(context.getString(R.string.tab_recent_played)))
                        add(recentlyPlayedAlbums)
                        add(recentlyPlayedArtists)
                    }
                    if (recentlyAddedAlbums.isNotEmpty() || recentlyAddedArtists.isNotEmpty()) {
                        add(Header(context.getString(R.string.tab_recent_added)))
                        add(recentlyAddedAlbums)
                        add(recentlyAddedArtists)
                    }
                    if (this.isEmpty()) {
                        add(HomeFragmentModel.Empty)
                    }
                }
            }
        }


    private fun List<Album>.toRecentlyPlayed(): HomeFragmentModel.RecentlyPlayedAlbums {
        return HomeFragmentModel.RecentlyPlayedAlbums(
            this.map { it.toItem() }
        )
    }

    private fun List<Album>.toRecentlyAdded(): HomeFragmentModel.RecentlyAddedAlbums {
        return HomeFragmentModel.RecentlyAddedAlbums(
            this.map { it.toItem() }
        )
    }

    private fun List<Artist>.toRecentlyPlayed(): HomeFragmentModel.RecentlyPlayedArtists {
        return HomeFragmentModel.RecentlyPlayedArtists(
            this.map { it.toItem() }
        )
    }

    private fun List<Artist>.toRecentlyAdded(): HomeFragmentModel.RecentlyAddedArtists {
        return HomeFragmentModel.RecentlyAddedArtists(
            this.map { it.toItem() }
        )
    }

    private fun Album.toItem(): Item {
        return Item(
            mediaId = this.mediaId.toPresentation(),
            title = this.title,
            subtitle = this.artist
        )
    }

    private fun Artist.toItem(): Item {
        return Item(
            mediaId = this.mediaId.toPresentation(),
            title = this.name,
            subtitle = DisplayableAlbum.readableSongCount(context.resources, this.songs)
        )
    }

}