package dev.olog.core.interactor.mostplayed

import dev.olog.core.MediaId.Track
import dev.olog.core.MediaIdCategory.*
import dev.olog.core.gateway.track.FolderGateway
import dev.olog.core.gateway.track.GenreGateway
import dev.olog.core.gateway.track.PlaylistGateway
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