package dev.olog.core.interactor.sort

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.sort.SortType
import dev.olog.core.executor.IoScheduler
import dev.olog.core.prefs.SortPreferences
import dev.olog.core.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class SetSortOrderUseCase @Inject constructor(
    schedulers: IoScheduler,
    private val gateway: SortPreferences

) : CompletableUseCaseWithParam<SetSortOrderRequestModel>(schedulers){

    override fun buildUseCaseObservable(param: SetSortOrderRequestModel): Completable {
        val category = param.mediaId.category
        return when (category){
            MediaIdCategory.FOLDERS -> gateway.setDetailFolderSortOrder(param.sortType)
            MediaIdCategory.PLAYLISTS,
            MediaIdCategory.PODCASTS_PLAYLIST -> gateway.setDetailPlaylistSortOrder(param.sortType)
            MediaIdCategory.ALBUMS,
            MediaIdCategory.PODCASTS_ALBUMS -> gateway.setDetailAlbumSortOrder(param.sortType)
            MediaIdCategory.ARTISTS,
            MediaIdCategory.PODCASTS_ARTISTS -> gateway.setDetailArtistSortOrder(param.sortType)
            MediaIdCategory.GENRES -> gateway.setDetailGenreSortOrder(param.sortType)
            else -> throw IllegalArgumentException("invalid param $param")
        }
    }
}

class SetSortOrderRequestModel(
    val mediaId: MediaId,
    val sortType: SortType
)