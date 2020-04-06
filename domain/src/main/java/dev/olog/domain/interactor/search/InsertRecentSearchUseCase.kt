package dev.olog.domain.interactor.search

import dev.olog.domain.MediaId
import dev.olog.domain.MediaId.Category
import dev.olog.domain.MediaId.Track
import dev.olog.domain.MediaIdCategory.*
import dev.olog.domain.gateway.RecentSearchesGateway
import dev.olog.shared.throwNotHandled
import javax.inject.Inject

class InsertRecentSearchUseCase @Inject constructor(
    private val recentSearchesGateway: RecentSearchesGateway

) {

    suspend operator fun invoke(mediaId: MediaId) {
        return when (mediaId) {
            is Track -> recentSearchesGateway.insertTrack(mediaId)
            is Category -> handleCategory(mediaId)
        }
    }

    private suspend fun handleCategory(mediaId: Category) {
        val id = mediaId.categoryId
        return when (mediaId.category) {
            FOLDERS -> recentSearchesGateway.insertFolder(id)
            PLAYLISTS -> recentSearchesGateway.insertPlaylist(id)
            ALBUMS -> recentSearchesGateway.insertAlbum(id)
            ARTISTS -> recentSearchesGateway.insertArtist(id)
            GENRES -> recentSearchesGateway.insertGenre(id)
            PODCASTS_PLAYLIST -> recentSearchesGateway.insertPodcastPlaylist(id)
            PODCASTS_AUTHORS -> recentSearchesGateway.insertPodcastArtist(id)
            PODCASTS, SONGS -> throwNotHandled(mediaId)
            SPOTIFY_ALBUMS -> throwNotHandled(mediaId)
            SPOTIFY_TRACK -> throwNotHandled(mediaId)
        }
    }
}