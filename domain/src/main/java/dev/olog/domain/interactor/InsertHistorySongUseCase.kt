package dev.olog.domain.interactor

import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.domain.gateway.track.PlaylistGateway
import dev.olog.domain.mediaid.MediaId
import javax.inject.Inject

class InsertHistorySongUseCase @Inject constructor(
    private val playlistGateway: PlaylistGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway

) {

    suspend operator fun invoke(mediaId: MediaId.Track) {
        if (mediaId.isAnyPodcast) {
            return podcastPlaylistGateway.insertPodcastToHistory(mediaId.id)
        }
        return playlistGateway.insertSongToHistory(mediaId.id)
    }

}