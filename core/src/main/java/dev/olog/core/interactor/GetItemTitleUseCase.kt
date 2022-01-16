package dev.olog.core.interactor

import dev.olog.core.author.AuthorGateway
import dev.olog.core.collection.CollectionGateway
import dev.olog.core.folder.FolderGateway
import dev.olog.core.genre.GenreGateway
import dev.olog.core.MediaUri
import dev.olog.core.playlist.PlaylistGateway
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetItemTitleUseCase @Inject constructor(
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val authorGateway: AuthorGateway,
    private val collectionGateway: CollectionGateway,
    private val genreGateway: GenreGateway,
) {

    operator fun invoke(uri: MediaUri): Flow<String> {
        return when (uri.category) {
            MediaUri.Category.Folder -> folderGateway.observeById(uri).map { it?.title.orEmpty() }
            MediaUri.Category.Author -> authorGateway.observeById(uri).map { it?.name.orEmpty() }
            MediaUri.Category.Collection -> collectionGateway.observeById(uri).map { it?.title.orEmpty() }
            MediaUri.Category.Playlist -> playlistGateway.observeById(uri).map { it?.title.orEmpty() }
            MediaUri.Category.Genre -> genreGateway.observeById(uri).map { it?.name.orEmpty() }
            MediaUri.Category.Track -> error("invalid $uri")
        }
    }

}