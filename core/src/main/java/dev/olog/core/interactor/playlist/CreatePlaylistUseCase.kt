package dev.olog.core.interactor.playlist

import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.track.PlaylistGateway
import dev.olog.core.gateway.track.SongGateway
import javax.inject.Inject

class CreatePlaylistUseCase @Inject constructor(
    private val songGateway: SongGateway,
    private val playlistGateway: PlaylistGateway,
) {

    @JvmName("createWithSongs")
    suspend operator fun invoke(title: String, songs: List<Song>): Int? {
        val playlistId = playlistGateway.createPlaylist(title) ?: return null
        return playlistGateway.addSongsToPlaylist(playlistId, songs)
    }

    @JvmName("createWithIds")
    suspend operator fun invoke(title: String, songIds: List<Long>): Int? {
        val songs = songIds.mapNotNull { songGateway.getById(it) }
        return invoke(title, songs)
    }

}