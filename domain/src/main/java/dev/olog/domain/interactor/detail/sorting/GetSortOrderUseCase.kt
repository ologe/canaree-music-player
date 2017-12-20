package dev.olog.domain.interactor.detail.sorting

import dev.olog.domain.entity.SortType
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.prefs.AppPreferencesGateway
import dev.olog.domain.interactor.base.FlowableUseCaseWithParam
import dev.olog.shared.MediaIdHelper
import io.reactivex.Flowable
import javax.inject.Inject

class GetSortOrderUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val gateway: AppPreferencesGateway

) : FlowableUseCaseWithParam<SortType, String>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: String): Flowable<SortType> {
        val category = MediaIdHelper.extractCategory(mediaId)
        return when (category){
            MediaIdHelper.MEDIA_ID_BY_FOLDER -> gateway.getFolderSortOrder()
            MediaIdHelper.MEDIA_ID_BY_PLAYLIST -> gateway.getPlaylistSortOrder()
            MediaIdHelper.MEDIA_ID_BY_ALBUM -> gateway.getAlbumSortOrder()
            MediaIdHelper.MEDIA_ID_BY_ARTIST -> gateway.getArtistSortOrder()
            MediaIdHelper.MEDIA_ID_BY_GENRE -> gateway.getGenreSortOrder()
            else -> throw IllegalArgumentException("invalid media id $mediaId")
        }
    }
}