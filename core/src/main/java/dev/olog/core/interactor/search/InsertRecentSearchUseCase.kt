package dev.olog.core.interactor.search

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.gateway.RecentSearchesGateway
import javax.inject.Inject

class InsertRecentSearchUseCase @Inject constructor(
    private val gateway: RecentSearchesGateway

) {

    suspend operator fun invoke(mediaId: MediaId) {
        val id = mediaId.id
        return when (mediaId.category) {
            MediaIdCategory.SONGS -> {
                if (mediaId.isPodcast) gateway.insertPodcast(id) else gateway.insertSong(id)
            }
            MediaIdCategory.ARTISTS -> {
                if (mediaId.isPodcast) gateway.insertPodcastArtist(id) else gateway.insertArtist(id)
            }
            MediaIdCategory.ALBUMS -> {
                if (mediaId.isPodcast) gateway.insertPodcastAlbum(id) else gateway.insertAlbum(id)
            }
            MediaIdCategory.PLAYLISTS -> {
                if (mediaId.isPodcast) gateway.insertPodcastPlaylist(id) else gateway.insertPlaylist(id)
            }
            MediaIdCategory.FOLDERS -> gateway.insertFolder(id)
            MediaIdCategory.GENRES -> gateway.insertGenre(id)
            MediaIdCategory.AUTO_PLAYLISTS,
            MediaIdCategory.HEADER,
            MediaIdCategory.PLAYING_QUEUE -> error("invalid media id $mediaId")
        }
    }
}