package dev.olog.msc.domain.interactor.item

import dev.olog.msc.domain.entity.Album
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.AlbumGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCaseUseCaseWithParam
import dev.olog.msc.utils.MediaId
import io.reactivex.Observable
import javax.inject.Inject

class GetAlbumUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: AlbumGateway

) : ObservableUseCaseUseCaseWithParam<Album, MediaId>(schedulers) {


    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<Album> {
        val albumId = mediaId.categoryValue.toLong()
        return gateway.getByParam(albumId)
    }
}
