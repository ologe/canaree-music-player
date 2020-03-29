package dev.olog.core.interactor.search

import dev.olog.core.MediaId
import dev.olog.core.MediaId.Category
import dev.olog.core.MediaId.Track
import dev.olog.core.MediaIdCategory.*
import dev.olog.core.gateway.RecentSearchesGateway
import dev.olog.shared.throwNotHandled
import javax.inject.Inject

class DeleteRecentSearchUseCase @Inject constructor(
    private val recentSearchesGateway: RecentSearchesGateway

)  {

    suspend operator fun invoke(mediaId: MediaId) {
        return when (mediaId) {
            is Track -> recentSearchesGateway.deleteTrack(mediaId)
            is Category -> handleCategory(mediaId)
        }
    }

    private suspend fun handleCategory(mediaId: Category) {
        val id = mediaId.categoryId
        return when (mediaId.category) {
            FOLDERS -> recentSearchesGateway.deleteFolder(id)
            PLAYLISTS -> recentSearchesGateway.deletePlaylist(id)
            ALBUMS -> recentSearchesGateway.deleteAlbum(id)
            ARTISTS -> recentSearchesGateway.deleteArtist(id)
            GENRES -> recentSearchesGateway.deleteGenre(id)
            PODCASTS_PLAYLIST -> recentSearchesGateway.deletePodcastPlaylist(id)
            PODCASTS_AUTHORS -> recentSearchesGateway.deletePodcastArtist(id)
            PODCASTS, SONGS -> throwNotHandled(mediaId)
            SPOTIFY_ALBUMS -> throwNotHandled(mediaId)
        }
    }

}