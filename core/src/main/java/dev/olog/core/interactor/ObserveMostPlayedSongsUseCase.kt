package dev.olog.core.interactor

import dev.olog.core.entity.MostPlayedSong
import dev.olog.core.folder.FolderGateway
import dev.olog.core.genre.GenreGateway
import dev.olog.core.MediaUri
import dev.olog.core.playlist.PlaylistGateway
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ObserveMostPlayedSongsUseCase @Inject constructor(
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val genreGateway: GenreGateway,
) {

    operator fun invoke(
        uri: MediaUri
    ): Flow<List<MostPlayedSong>> {
        return when (uri.category) {
            MediaUri.Category.Folder -> folderGateway.observeMostPlayed(uri)
            MediaUri.Category.Playlist -> playlistGateway.observeMostPlayed(uri)
            MediaUri.Category.Genre -> genreGateway.observeMostPlayed(uri)
            MediaUri.Category.Track,
            MediaUri.Category.Author,
            MediaUri.Category.Collection -> flowOf(emptyList())
        }
    }
}