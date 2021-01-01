package dev.olog.domain.interactor.search

import dev.olog.domain.gateway.RecentSearchesGateway
import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.mediaid.MediaIdCategory
import dev.olog.shared.exhaustive
import javax.inject.Inject

class DeleteRecentSearchUseCase @Inject constructor(
    private val recentSearchesGateway: RecentSearchesGateway

)  {

    suspend operator fun invoke(mediaId: MediaId) {
        when (mediaId) {
            is MediaId.Category -> handleCategory(mediaId)
            is MediaId.Track -> handleTrack(mediaId)
        }.exhaustive
    }

    private suspend fun handleTrack(mediaId: MediaId.Track) {
        if (mediaId.isAnyPodcast) {
            return recentSearchesGateway.deletePodcast(mediaId.id)
        }
        return recentSearchesGateway.deleteSong(mediaId.id)
    }

    private suspend fun handleCategory(mediaId: MediaId.Category) {
        when (mediaId.category) {
            MediaIdCategory.FOLDERS -> recentSearchesGateway.deleteFolder(mediaId.hashCode().toLong())
            MediaIdCategory.PLAYLISTS -> recentSearchesGateway.deletePlaylist(mediaId.categoryValue.toLong())
            MediaIdCategory.ALBUMS -> recentSearchesGateway.deleteAlbum(mediaId.categoryValue.toLong())
            MediaIdCategory.ARTISTS -> recentSearchesGateway.deleteArtist(mediaId.categoryValue.toLong())
            MediaIdCategory.GENRES -> recentSearchesGateway.deleteGenre(mediaId.categoryValue.toLong())
            MediaIdCategory.PODCASTS_PLAYLIST -> recentSearchesGateway.deletePodcastPlaylist(mediaId.categoryValue.toLong())
            MediaIdCategory.PODCASTS_ALBUMS -> recentSearchesGateway.deletePodcastAlbum(mediaId.categoryValue.toLong())
            MediaIdCategory.PODCASTS_ARTISTS -> recentSearchesGateway.deletePodcastArtist(mediaId.categoryValue.toLong())

            MediaIdCategory.PODCASTS,
            MediaIdCategory.SONGS -> error("invalid mediaid=$mediaId")
        }.exhaustive
    }

}