package dev.olog.core.sort

import dev.olog.core.MediaStoreType
import dev.olog.core.author.AuthorGateway
import dev.olog.core.collection.CollectionGateway
import dev.olog.core.folder.FolderGateway
import dev.olog.core.genre.GenreGateway
import dev.olog.core.MediaUri
import dev.olog.core.playlist.PlaylistGateway
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveDetailSortUseCase @Inject constructor(
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val authorGateway: AuthorGateway,
    private val collectionGateway: CollectionGateway,
    private val genreGateway: GenreGateway,
) {

    operator fun invoke(
        category: MediaUri.Category,
        type: MediaStoreType,
    ): Flow<Sort<SortType>> {
        return when (category) {
            MediaUri.Category.Folder -> folderGateway.observeDetailSort()
            MediaUri.Category.Playlist -> playlistGateway.observeDetailSort(type)
            MediaUri.Category.Author -> authorGateway.observeDetailSort(type)
            MediaUri.Category.Collection -> collectionGateway.observeDetailSort(type)
            MediaUri.Category.Genre -> genreGateway.observeDetailSort()
            MediaUri.Category.Track -> error("invalid")
        }
    }

}