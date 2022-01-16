package dev.olog.core.playlist

import dev.olog.core.MediaUri
import javax.inject.Inject

class ClearPlaylistUseCase @Inject constructor(
    private val playlistGateway: PlaylistGateway,
) {

    suspend operator fun invoke(uri: MediaUri) {
        playlistGateway.clear(uri)
    }
}