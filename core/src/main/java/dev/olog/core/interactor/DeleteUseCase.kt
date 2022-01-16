package dev.olog.core.interactor

import dev.olog.core.MediaUri
import dev.olog.core.track.TrackGateway
import dev.olog.core.playlist.PlaylistGateway
import javax.inject.Inject

class DeleteUseCase @Inject constructor(
    private val playableGateway: TrackGateway,
    private val playlistGateway: PlaylistGateway,
    private val tracksByCategoryIdUseCase: GetTracksByCategoryUseCase,
) {

    suspend operator fun invoke(uri: MediaUri) {
        when (uri.category) {
            MediaUri.Category.Track -> playableGateway.delete(listOf(uri))
            MediaUri.Category.Playlist -> playlistGateway.delete(uri)
            else -> {
                val uris = tracksByCategoryIdUseCase(uri).map { it.uri }
                playableGateway.delete(uris)
            }
        }
    }
}