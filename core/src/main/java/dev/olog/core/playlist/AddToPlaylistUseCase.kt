package dev.olog.core.playlist

import dev.olog.core.MediaUri
import dev.olog.core.interactor.GetTracksByCategoryUseCase
import javax.inject.Inject

class AddToPlaylistUseCase @Inject constructor(
    private val playlistGateway: PlaylistGateway,
    private val categoryPlayablesByIdUseCase: GetTracksByCategoryUseCase,
) {

    suspend operator fun invoke(playlist: Playlist, uri: MediaUri) {
        val uris = when (uri.category) {
            MediaUri.Category.Track -> listOf(uri)
            else -> categoryPlayablesByIdUseCase(uri).map { it.uri }
        }
        playlistGateway.addTracks(playlist.uri, uris)
    }
}