package dev.olog.domain.interactor.playlist

import dev.olog.domain.entity.PlaylistType
import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.domain.gateway.track.PlaylistGateway
import javax.inject.Inject

class RemoveFromPlaylistUseCase @Inject constructor(
    private val playlistGateway: PlaylistGateway,
    private val podcastGateway: PodcastPlaylistGateway

) {

    suspend operator fun invoke(input: Input) {
        return when (input.type) {
            PlaylistType.PODCAST -> podcastGateway.removeFromPlaylist(
                input.playlistId,
                input.idInPlaylist
            )
            PlaylistType.TRACK -> playlistGateway.removeFromPlaylist(
                input.playlistId,
                input.idInPlaylist
            )
        }
    }

    class Input(
        val playlistId: Long,
        val idInPlaylist: Long,
        val type: PlaylistType
    )

}