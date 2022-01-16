package dev.olog.core.interactor

import dev.olog.core.folder.FolderGateway
import dev.olog.core.genre.GenreGateway
import dev.olog.core.MediaUri
import dev.olog.core.track.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ObserveRecentlyAddedUseCase @Inject constructor(
    private val folderGateway: FolderGateway,
    private val genreGateway: GenreGateway,
) {

    operator fun invoke(
        uri: MediaUri,
    ): Flow<List<Song>> {
        return when (uri.category) {
            MediaUri.Category.Folder -> folderGateway.observeRecentlyAddedTracksById(uri)
            MediaUri.Category.Genre -> genreGateway.observeRecentlyAddedTracksById(uri)
            MediaUri.Category.Playlist,
            MediaUri.Category.Track,
            MediaUri.Category.Author,
            MediaUri.Category.Collection -> flowOf(emptyList())
        }
    }
}