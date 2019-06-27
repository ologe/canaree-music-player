package dev.olog.core.interactor

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.FolderGateway
import dev.olog.core.gateway.GenreGateway
import dev.olog.core.interactor.base.FlowUseCaseWithParam
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ObserveRecentlyAddedUseCase @Inject constructor(
    private val folderGateway: FolderGateway,
    private val genreGateway: GenreGateway

) : FlowUseCaseWithParam<List<Song>, MediaId>() {

    override fun buildUseCase(mediaId: MediaId): Flow<List<Song>> {
        return when (mediaId.category){
            MediaIdCategory.FOLDERS -> folderGateway.observeRecentlyAdded(mediaId.categoryValue)
            MediaIdCategory.GENRES -> genreGateway.observeRecentlyAdded(mediaId.categoryId)
            else -> flow {  }
        }
    }
}