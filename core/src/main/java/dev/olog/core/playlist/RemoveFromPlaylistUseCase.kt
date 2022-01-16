package dev.olog.core.playlist

import dev.olog.core.MediaUri
import javax.inject.Inject

class RemoveFromPlaylistUseCase @Inject constructor(
    private val playlistGateway: PlaylistGateway,
) {

    suspend operator fun invoke(
        uri: MediaUri,
        position: Int,
    ) {
        playlistGateway.remove(uri, position)
    }

}