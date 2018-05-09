package dev.olog.msc.domain.interactor.item

import dev.olog.msc.domain.entity.Genre
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.GenreGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCaseUseCaseWithParam
import dev.olog.msc.utils.MediaId
import io.reactivex.Observable
import javax.inject.Inject

class GetGenreUseCase @Inject internal constructor(
        schedulers: IoScheduler,
        private val gateway: GenreGateway

) : ObservableUseCaseUseCaseWithParam<Genre, MediaId>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(mediaId: MediaId): Observable<Genre> {
        val genreId = mediaId.categoryValue.toLong()

        return gateway.getByParam(genreId)
    }
}
