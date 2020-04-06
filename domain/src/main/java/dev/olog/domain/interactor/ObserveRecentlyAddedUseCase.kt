package dev.olog.domain.interactor

import dev.olog.domain.MediaId.Category
import dev.olog.domain.MediaIdCategory.FOLDERS
import dev.olog.domain.MediaIdCategory.GENRES
import dev.olog.domain.entity.track.Song
import dev.olog.domain.gateway.track.FolderGateway
import dev.olog.domain.gateway.track.GenreGateway
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