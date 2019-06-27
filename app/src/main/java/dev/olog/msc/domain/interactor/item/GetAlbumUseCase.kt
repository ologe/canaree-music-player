package dev.olog.msc.domain.interactor.item

import dev.olog.core.MediaId
import dev.olog.core.entity.track.Album
import dev.olog.core.executor.IoScheduler
import dev.olog.core.gateway.AlbumGateway
import dev.olog.core.interactor.base.ObservableUseCaseWithParam
import io.reactivex.Observable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.rx2.asObservable
import javax.inject.Inject

class GetAlbumUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: AlbumGateway

) : ObservableUseCaseWithParam<Album, MediaId>(schedulers) {


    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<Album> {
        return gateway.observeByParam(mediaId.categoryId).map { it!! }
                .asObservable()
    }
}
