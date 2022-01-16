package dev.olog.core.playlist

import dev.olog.core.MediaStoreType
import javax.inject.Inject

class GetPlaylistsUseCase @Inject internal constructor(
    private val playlistGateway: PlaylistGateway,
) {

    operator fun invoke(type: MediaStoreType): List<Playlist> {
        return playlistGateway.getAll(type)
    }
}
