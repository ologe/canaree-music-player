package dev.olog.domain.interactor.playlist

import dev.olog.domain.entity.PlaylistType
import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.domain.gateway.track.PlaylistGateway
import dev.olog.shared.exhaustive
import javax.inject.Inject

class RemoveFromPlaylistUseCase @Inject constructor(
    private val playlistGateway: PlaylistGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway
) {

    suspend operator fun invoke(
        playlistId: Long,
        type: PlaylistType,
        idInPlaylist: Long,
    ) {
        when (type) {
            PlaylistType.TRACK -> playlistGateway.removeFromPlaylist(playlistId, idInPlaylist)
            PlaylistType.PODCAST -> podcastPlaylistGateway.removeFromPlaylist(playlistId, idInPlaylist)
            PlaylistType.AUTO -> error("invalid type=$type")
        }.exhaustive
    }

}