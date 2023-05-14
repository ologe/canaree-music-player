package dev.olog.core.interactor.playlist

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.track.Playlist
import dev.olog.core.gateway.track.PlaylistGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.core.interactor.songlist.GetSongListByParamUseCase
import javax.inject.Inject

class AddToPlaylistUseCase @Inject constructor(
    private val playlistGateway: PlaylistGateway,
    private val songGateway: SongGateway,
    private val getSongListByParamUseCase: GetSongListByParamUseCase

) {

    suspend operator fun invoke(playlist: Playlist, mediaId: MediaId) {
        val songs = when (mediaId.category) {
            MediaIdCategory.SONGS -> listOfNotNull(songGateway.getById(mediaId.id))
            else -> getSongListByParamUseCase(mediaId)
        }
        playlistGateway.addSongsToPlaylist(
            playlistId = playlist.id,
            songs = songs,
        )
    }
}