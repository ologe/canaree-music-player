package dev.olog.core.interactor

import dev.olog.core.author.Artist
import dev.olog.core.folder.FolderGateway
import dev.olog.core.genre.GenreGateway
import dev.olog.core.MediaUri
import dev.olog.core.playlist.PlaylistGateway
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ObserveRelatedArtistsUseCase @Inject constructor(
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val genreGateway: GenreGateway,
) {

    operator fun invoke(
        uri: MediaUri
    ): Flow<List<Artist>> {
        return when (uri.category) {
            MediaUri.Category.Folder -> folderGateway.observeRelatedArtistsById(uri)
            MediaUri.Category.Playlist -> playlistGateway.observeRelatedArtistsById(uri)
            MediaUri.Category.Genre -> genreGateway.observeRelatedArtistsById(uri)
            MediaUri.Category.Track,
            MediaUri.Category.Author,
            MediaUri.Category.Collection -> flowOf(emptyList())
        }
    }
}