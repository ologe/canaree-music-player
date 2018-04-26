package dev.olog.msc.domain.interactor.detail.sorting

import dev.olog.msc.domain.entity.SortType
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCaseUseCaseWithParam
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.MediaIdCategory
import io.reactivex.Observable
import javax.inject.Inject

class GetSortOrderUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val gateway: AppPreferencesGateway

) : ObservableUseCaseUseCaseWithParam<SortType, MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<SortType> {
        val category = mediaId.category
        return when (category){
            MediaIdCategory.FOLDERS -> gateway.getFolderSortOrder()
            MediaIdCategory.PLAYLISTS -> gateway.getPlaylistSortOrder()
            MediaIdCategory.ALBUMS -> gateway.getAlbumSortOrder()
            MediaIdCategory.ARTISTS -> gateway.getArtistSortOrder()
            MediaIdCategory.GENRES -> gateway.getGenreSortOrder()
            else -> throw IllegalArgumentException("invalid media id $mediaId")
        }
    }
}