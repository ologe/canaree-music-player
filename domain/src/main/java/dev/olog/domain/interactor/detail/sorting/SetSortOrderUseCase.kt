package dev.olog.domain.interactor.detail.sorting

import dev.olog.domain.entity.SortType
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.domain.interactor.base.CompletableUseCaseWithParam
import dev.olog.shared.MediaId
import dev.olog.shared.MediaIdCategory
import io.reactivex.Completable
import javax.inject.Inject

class SetSortOrderUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val gateway: AppPreferencesGateway

) : CompletableUseCaseWithParam<SetSortOrderRequestModel>(schedulers){

    override fun buildUseCaseObservable(param: SetSortOrderRequestModel): Completable {
        val category = param.mediaId.category
        return when (category){
            MediaIdCategory.FOLDER -> gateway.setFolderSortOrder(param.sortType)
            MediaIdCategory.PLAYLIST -> gateway.setPlaylistSortOrder(param.sortType)
            MediaIdCategory.ALBUM -> gateway.setAlbumSortOrder(param.sortType)
            MediaIdCategory.ARTIST -> gateway.setArtistSortOrder(param.sortType)
            MediaIdCategory.GENRE -> gateway.setGenreSortOrder(param.sortType)
            else -> throw IllegalArgumentException("invalid param $param")
        }
    }
}

class SetSortOrderRequestModel(
        val mediaId: MediaId,
        val sortType: SortType
)