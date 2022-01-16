package dev.olog.core.sort

import dev.olog.core.MediaStoreType
import dev.olog.core.author.AuthorGateway
import dev.olog.core.collection.CollectionGateway
import dev.olog.core.folder.FolderGateway
import dev.olog.core.genre.GenreGateway
import dev.olog.core.MediaUri
import dev.olog.core.playlist.PlaylistGateway
import javax.inject.Inject

class GetDetailSortUseCase @Inject constructor(
    private val folderGateway: FolderGateway,
    private val playlistGateway: PlaylistGateway,
    private val authorGateway: AuthorGateway,
    private val collectionGateway: CollectionGateway,
    private val genreGateway: GenreGateway,
) {

    operator fun invoke(
        category: MediaUri.Category,
        type: MediaStoreType,
    ): Sort<SortType> {
        return when (category) {
            MediaUri.Category.Folder -> folderGateway.getDetailSort()
            MediaUri.Category.Playlist -> playlistGateway.getDetailSort(type)
            MediaUri.Category.Author -> authorGateway.getDetailSort(type)
            MediaUri.Category.Collection -> collectionGateway.getDetailSort(type)
            MediaUri.Category.Genre -> genreGateway.getDetailSort()
            MediaUri.Category.Track -> error("invalid")
        }
    }

}