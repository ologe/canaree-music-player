package dev.olog.domain.interactor.detail.sorting

import dev.olog.domain.entity.SortType
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.domain.interactor.base.CompletableUseCaseWithParam
import dev.olog.shared.MediaIdHelper
import io.reactivex.Completable
import javax.inject.Inject

class SetSortOrderUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val gateway: AppPreferencesGateway

) : CompletableUseCaseWithParam<SetSortOrderRequestModel>(schedulers){

    override fun buildUseCaseObservable(param: SetSortOrderRequestModel): Completable {
        val category = MediaIdHelper.extractCategory(param.mediaId)
        return when (category){
            MediaIdHelper.MEDIA_ID_BY_FOLDER -> gateway.setFolderSortOrder(param.sortType)
            MediaIdHelper.MEDIA_ID_BY_PLAYLIST -> gateway.setPlaylistSortOrder(param.sortType)
            MediaIdHelper.MEDIA_ID_BY_ALBUM -> gateway.setAlbumSortOrder(param.sortType)
            MediaIdHelper.MEDIA_ID_BY_ARTIST -> gateway.setArtistSortOrder(param.sortType)
            MediaIdHelper.MEDIA_ID_BY_GENRE -> gateway.setGenreSortOrder(param.sortType)
            else -> throw IllegalArgumentException("invalid param $param")
        }
    }
}

class SetSortOrderRequestModel(
        val mediaId: String,
        val sortType: SortType
)