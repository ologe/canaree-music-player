package dev.olog.core.interactor

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
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

    operator fun invoke(mediaId: MediaId): Flow<List<Song>> {
        return when (mediaId.category){
            MediaIdCategory.FOLDERS -> folderGateway.observeRecentlyAdded(mediaId.categoryId)
            MediaIdCategory.GENRES -> genreGateway.observeRecentlyAdded(mediaId.categoryId)
            else -> flowOf(listOf())
        }
    }
}