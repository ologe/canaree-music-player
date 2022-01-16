package dev.olog.core.playlist

import dev.olog.core.MediaUri
import javax.inject.Inject

class RenameUseCase @Inject constructor(
    private val playlistGateway: PlaylistGateway,
) {

    suspend operator fun invoke(uri: MediaUri, newTitle: String) {
        playlistGateway.rename(uri, newTitle)
    }
}