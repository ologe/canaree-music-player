package dev.olog.core.interactor

import dev.olog.core.MediaId.Category
import dev.olog.core.MediaIdCategory.FOLDERS
import dev.olog.core.MediaIdCategory.GENRES
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.track.FolderGateway
import dev.olog.core.gateway.track.GenreGateway
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ObserveRecentlyAddedUseCase @Inject constructor(
    private val folderGateway: FolderGateway,
    private val genreGateway: GenreGateway

) {

    operator fun invoke(mediaId: Category): Flow<List<Song>> {
        return when (mediaId.category){
            FOLDERS -> folderGateway.observeRecentlyAdded(mediaId.categoryId)
            GENRES -> genreGateway.observeRecentlyAdded(mediaId.categoryId.toLong())
            else -> flowOf(listOf())
        }
    }
}