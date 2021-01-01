package dev.olog.core.interactor

import dev.olog.core.mediaid.MediaId
import dev.olog.core.mediaid.MediaIdCategory
import dev.olog.core.entity.track.Track
import dev.olog.core.gateway.track.FolderGateway
import dev.olog.core.gateway.track.GenreGateway
import dev.olog.core.interactor.base.FlowUseCaseWithParam
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ObserveRecentlyAddedUseCase @Inject constructor(
    private val folderGateway: FolderGateway,
    private val genreGateway: GenreGateway

) : FlowUseCaseWithParam<List<Track>, MediaId>() {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCase(mediaId: MediaId): Flow<List<Track>> {
        return when (mediaId.category){
            MediaIdCategory.FOLDERS -> folderGateway.observeRecentlyAdded(mediaId.categoryValue)
            MediaIdCategory.GENRES -> genreGateway.observeRecentlyAdded(mediaId.categoryId)
            else -> flowOf(listOf())
        }
    }
}