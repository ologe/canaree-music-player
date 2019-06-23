package dev.olog.msc.domain.interactor.item

import dev.olog.core.entity.track.Genre
import dev.olog.core.executor.IoScheduler
import dev.olog.msc.domain.gateway.GenreGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCaseWithParam
import dev.olog.core.MediaId
import io.reactivex.Observable
import javax.inject.Inject

class GetGenreUseCase @Inject internal constructor(
    schedulers: IoScheduler,
    private val gateway: GenreGateway

) : ObservableUseCaseWithParam<Genre, MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<Genre> {
        return gateway.getByParam(mediaId.categoryId)
    }
}
