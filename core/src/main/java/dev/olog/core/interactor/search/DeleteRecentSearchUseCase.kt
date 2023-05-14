package dev.olog.core.interactor.search

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.gateway.RecentSearchesGateway
import javax.inject.Inject

class DeleteRecentSearchUseCase @Inject constructor(
    private val gateway: RecentSearchesGateway

)  {

    suspend operator fun invoke(mediaId: MediaId) {
        val id = mediaId.id
        return when (mediaId.category) {
            MediaIdCategory.SONGS -> {
                if (mediaId.isPodcast) gateway.deletePodcast(id) else gateway.deleteSong(id)
            }
            MediaIdCategory.ARTISTS -> {
                if (mediaId.isPodcast) gateway.deletePodcastArtist(id) else gateway.deleteArtist(id)
            }
            MediaIdCategory.ALBUMS -> {
                if (mediaId.isPodcast) gateway.deletePodcastAlbum(id) else gateway.deleteAlbum(id)
            }
            MediaIdCategory.PLAYLISTS -> {
                if (mediaId.isPodcast) gateway.deletePodcastPlaylist(id) else gateway.deletePlaylist(id)
            }
            MediaIdCategory.FOLDERS -> gateway.deleteFolder(id)
            MediaIdCategory.GENRES -> gateway.deleteGenre(id)
            MediaIdCategory.AUTO_PLAYLISTS,
            MediaIdCategory.HEADER,
            MediaIdCategory.PLAYING_QUEUE -> error("invalid media id $mediaId")
        }
    }
}