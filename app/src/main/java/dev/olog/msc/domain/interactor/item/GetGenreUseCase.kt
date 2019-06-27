package dev.olog.msc.domain.interactor.item

import dev.olog.core.MediaId
import dev.olog.core.entity.track.Genre
import dev.olog.core.executor.IoScheduler
import dev.olog.core.gateway.GenreGateway
import dev.olog.core.interactor.base.ObservableUseCaseWithParam
import io.reactivex.Observable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.rx2.asObservable
import javax.inject.Inject

class GetGenreUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: GenreGateway

) : ObservableUseCaseWithParam<Genre, MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<Genre> {
        return gateway.observeByParam(mediaId.categoryId).map { it!! }.asObservable()
    }
}
