package dev.olog.core.playlist

import dev.olog.core.MediaUri
import javax.inject.Inject

class MoveItemInPlaylistUseCase @Inject constructor(
    private val playlistGateway: PlaylistGateway,
) {

    suspend fun execute(
        uri: MediaUri,
        moveList: List<Pair<Int, Int>>,
    ) {
        playlistGateway.moveItem(uri, moveList)
    }

}