package dev.olog.core.interactor

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.gateway.track.FolderGateway
import dev.olog.core.gateway.track.GenreGateway
import dev.olog.core.gateway.track.PlaylistGateway
import javax.inject.Inject

class InsertMostPlayedUseCase @Inject constructor(
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val genreGateway: GenreGateway

) {

    suspend operator fun invoke(
        parentMediaId: MediaId?,
        mediaId: MediaId
    ) {
        when (mediaId.category) {
            MediaIdCategory.FOLDERS -> folderGateway.insertMostPlayed(
                parentMediaId = parentMediaId ?: return,
                mediaId = mediaId
            )
            MediaIdCategory.PLAYLISTS -> playlistGateway.insertMostPlayed(
                parentMediaId = parentMediaId ?: return,
                mediaId = mediaId
            )
            MediaIdCategory.GENRES -> genreGateway.insertMostPlayed(
                parentMediaId = parentMediaId ?: return,
                mediaId = mediaId
            )
            else -> return
        }
    }

}