package dev.olog.core.playlist

import dev.olog.core.MediaUri
import javax.inject.Inject

class RemoveDuplicatesUseCase @Inject constructor(
    private val playlistGateway: PlaylistGateway,
) {

    suspend operator fun invoke(uri: MediaUri) {
        playlistGateway.removeDuplicated(uri)
    }
}