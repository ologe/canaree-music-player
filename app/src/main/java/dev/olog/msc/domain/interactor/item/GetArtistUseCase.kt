package dev.olog.msc.domain.interactor.item

import dev.olog.msc.domain.entity.Artist
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.ArtistGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCaseUseCaseWithParam
import dev.olog.msc.utils.MediaId
import io.reactivex.Observable
import javax.inject.Inject

class GetArtistUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: ArtistGateway

) : ObservableUseCaseUseCaseWithParam<Artist, MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<Artist> {
        return gateway.getByParam(mediaId.resolveId)
    }
}
