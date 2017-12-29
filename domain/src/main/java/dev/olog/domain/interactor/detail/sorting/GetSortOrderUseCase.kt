package dev.olog.domain.interactor.detail.sorting

import dev.olog.domain.entity.SortType
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.shared.MediaId
import dev.olog.shared.MediaIdCategory
import io.reactivex.Flowable
import javax.inject.Inject

class GetSortOrderUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val gateway: AppPreferencesGateway

) : FlowableUseCaseWithParam<SortType, MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Flowable<SortType> {
        val category = mediaId.category
        return when (category){
            MediaIdCategory.FOLDER -> gateway.getFolderSortOrder()
            MediaIdCategory.PLAYLIST -> gateway.getPlaylistSortOrder()
            MediaIdCategory.ALBUM -> gateway.getAlbumSortOrder()
            MediaIdCategory.ARTIST -> gateway.getArtistSortOrder()
            MediaIdCategory.GENRE -> gateway.getGenreSortOrder()
            else -> throw IllegalArgumentException("invalid media id $mediaId")
        }
    }
}