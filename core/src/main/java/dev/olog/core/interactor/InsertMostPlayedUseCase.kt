package dev.olog.core.interactor

import dev.olog.core.folder.FolderGateway
import dev.olog.core.genre.GenreGateway
import dev.olog.core.MediaUri
import dev.olog.core.playlist.PlaylistGateway
import javax.inject.Inject

class InsertMostPlayedUseCase @Inject constructor(
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val genreGateway: GenreGateway,
) {

    suspend operator fun invoke(
        uri: MediaUri,
        trackUri: MediaUri,
    ) {
        return when (uri.category) {
            MediaUri.Category.Folder -> folderGateway.insertMostPlayed(uri, trackUri)
            MediaUri.Category.Playlist -> playlistGateway.insertMostPlayed(uri, trackUri)
            MediaUri.Category.Genre -> genreGateway.insertMostPlayed(uri, trackUri)
            MediaUri.Category.Track,
            MediaUri.Category.Author,
            MediaUri.Category.Collection -> error("invalid $uri")
        }
    }

}