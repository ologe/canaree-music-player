package dev.olog.domain.interactor.detail

import dev.olog.domain.entity.Genre
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.GenreGateway
import dev.olog.domain.interactor.base.FlowableUseCase
import javax.inject.Inject

class GetGenreSiblingsUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val gateway: GenreGateway

) : FlowableUseCase<List<Genre>>(schedulers) {


    override fun buildUseCaseObservable() = gateway.getAll()
}
