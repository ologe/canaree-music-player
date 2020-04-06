package dev.olog.domain.interactor.mostplayed

import dev.olog.domain.MediaId.Track
import dev.olog.domain.MediaIdCategory.*
import dev.olog.domain.gateway.track.FolderGateway
import dev.olog.domain.gateway.track.GenreGateway
import dev.olog.domain.gateway.track.PlaylistGateway
import javax.inject.Inject

class InsertMostPlayedUseCase @Inject constructor(
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val genreGateway: GenreGateway

) {

    suspend operator fun invoke(mediaId: Track) {
        when (mediaId.category) {
            FOLDERS -> folderGateway.insertMostPlayed(mediaId)
            PLAYLISTS -> playlistGateway.insertMostPlayed(mediaId)
            GENRES -> genreGateway.insertMostPlayed(mediaId)
            else -> return
        }
    }

}