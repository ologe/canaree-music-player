package dev.olog.core.playlist

import dev.olog.core.MediaUri
import javax.inject.Inject

class InsertCustomTrackListToPlaylist @Inject constructor(
    private val playlistGateway: PlaylistGateway,
) {

    suspend operator fun invoke(
        title: String,
        tracksUri: List<MediaUri>,
    ) {
        playlistGateway.createPlaylist(title, tracksUri)
    }
}