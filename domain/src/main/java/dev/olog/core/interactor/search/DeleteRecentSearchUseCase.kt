package dev.olog.core.interactor.search

import dev.olog.core.MediaId
import dev.olog.core.gateway.RecentSearchesGateway
import javax.inject.Inject

class DeleteRecentSearchUseCase @Inject constructor(
    private val recentSearchesGateway: RecentSearchesGateway

)  {

    suspend operator fun invoke(mediaId: MediaId) {
        val id = mediaId.resolveId
        return when {
            mediaId.isLeaf && !mediaId.isPodcast -> recentSearchesGateway.deleteSong(id)
            mediaId.isArtist -> recentSearchesGateway.deleteArtist(id)
            mediaId.isAlbum -> recentSearchesGateway.deleteAlbum(id)
            mediaId.isPlaylist -> recentSearchesGateway.deletePlaylist(id)
            mediaId.isFolder -> recentSearchesGateway.deleteFolder(id)
            mediaId.isGenre -> recentSearchesGateway.deleteGenre(id)

            mediaId.isLeaf && mediaId.isPodcast -> recentSearchesGateway.deletePodcast(id)
            mediaId.isPodcastPlaylist -> recentSearchesGateway.deletePodcastPlaylist(id)
            mediaId.isPodcastAlbum -> recentSearchesGateway.deletePodcastAlbum(id)
            mediaId.isPodcastArtist -> recentSearchesGateway.deletePodcastArtist(id)
            else -> throw IllegalArgumentException("invalid category ${mediaId.resolveId}")
        }
    }
}