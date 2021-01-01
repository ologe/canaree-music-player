package dev.olog.domain.interactor.playlist

import dev.olog.domain.entity.PlaylistType
import dev.olog.domain.gateway.podcast.PodcastPlaylistGateway
import dev.olog.domain.gateway.track.PlaylistGateway
import dev.olog.shared.exhaustive
import javax.inject.Inject

class MoveItemInPlaylistUseCase @Inject constructor(
    private val playlistGateway: PlaylistGateway,
    private val podcastPlaylistGateway: PodcastPlaylistGateway
) {

    suspend fun execute(
        playlistId: Long,
        type: PlaylistType,
        moveList: List<Pair<Int, Int>>,
    ) {
        when (type) {
            PlaylistType.TRACK -> playlistGateway.moveItem(playlistId, moveList)
            PlaylistType.PODCAST -> podcastPlaylistGateway.moveItem(playlistId, moveList)
            PlaylistType.AUTO -> error("invalid type=$type")
        }.exhaustive
    }

}