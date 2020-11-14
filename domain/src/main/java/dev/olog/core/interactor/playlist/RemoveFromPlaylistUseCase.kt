package dev.olog.core.interactor.playlist

import dev.olog.core.entity.PlaylistType
import dev.olog.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.core.gateway.track.PlaylistGateway
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoveFromPlaylistUseCase @Inject constructor(
    private val playlistGateway: PlaylistGateway,
    private val podcastGateway: PodcastPlaylistGateway

) {

    suspend operator fun invoke(input: Input) = withContext(Dispatchers.Default){

        if (input.type == PlaylistType.PODCAST){
            podcastGateway.removeFromPlaylist(input.playlistId, input.idInPlaylist)
        } else {
            playlistGateway.removeFromPlaylist(input.playlistId, input.idInPlaylist)
        }
    }

    class Input(
            val playlistId: Long,
            val idInPlaylist: Long,
            val type: PlaylistType
    )

}