package dev.olog.msc.domain.interactor.item

import dev.olog.core.entity.track.Artist
import dev.olog.core.executor.IoScheduler
import dev.olog.msc.domain.interactor.base.ObservableUseCaseWithParam
import dev.olog.core.MediaId
import dev.olog.core.gateway.ArtistGateway
import io.reactivex.Observable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.rx2.asObservable
import javax.inject.Inject

class GetArtistUseCase @Inject internal constructor(
    schedulers: IoScheduler,
    private val gateway: ArtistGateway

) : ObservableUseCaseWithParam<Artist, MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<Artist> {
        return gateway.observeByParam(mediaId.categoryId).map { it!! }.asObservable()
    }
}
