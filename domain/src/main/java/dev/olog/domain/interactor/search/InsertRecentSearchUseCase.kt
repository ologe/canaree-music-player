package dev.olog.domain.interactor.search

import dev.olog.domain.gateway.RecentSearchesGateway
import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.mediaid.MediaIdCategory
import dev.olog.shared.exhaustive
import javax.inject.Inject

class InsertRecentSearchUseCase @Inject constructor(
    private val recentSearchesGateway: RecentSearchesGateway
) {

    suspend operator fun invoke(mediaId: MediaId) {
        when (mediaId) {
            is MediaId.Category -> handleCategory(mediaId)
            is MediaId.Track -> handleTrack(mediaId)
        }.exhaustive
    }

    private suspend fun handleTrack(mediaId: MediaId.Track) {
        if (mediaId.isAnyPodcast) {
            return recentSearchesGateway.insertPodcast(mediaId.id)
        }
        return recentSearchesGateway.insertSong(mediaId.id)
    }

    private suspend fun handleCategory(mediaId: MediaId.Category) {
        when (mediaId.category) {
            MediaIdCategory.FOLDERS -> recentSearchesGateway.insertFolder(
                mediaId.hashCode().toLong()
            )
            MediaIdCategory.PLAYLISTS -> recentSearchesGateway.insertPlaylist(mediaId.categoryValue.toLong())
            MediaIdCategory.ALBUMS -> recentSearchesGateway.insertAlbum(mediaId.categoryValue.toLong())
            MediaIdCategory.ARTISTS -> recentSearchesGateway.insertArtist(mediaId.categoryValue.toLong())
            MediaIdCategory.GENRES -> recentSearchesGateway.insertGenre(mediaId.categoryValue.toLong())
            MediaIdCategory.PODCASTS_PLAYLIST -> recentSearchesGateway.insertPodcastPlaylist(mediaId.categoryValue.toLong())
            MediaIdCategory.PODCASTS_ALBUMS -> recentSearchesGateway.insertPodcastAlbum(mediaId.categoryValue.toLong())
            MediaIdCategory.PODCASTS_ARTISTS -> recentSearchesGateway.insertPodcastArtist(mediaId.categoryValue.toLong())

            MediaIdCategory.PODCASTS,
            MediaIdCategory.SONGS -> error("invalid mediaid=$mediaId")
        }.exhaustive
    }
}