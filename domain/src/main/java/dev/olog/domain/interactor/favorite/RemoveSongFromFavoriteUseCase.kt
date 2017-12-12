package dev.olog.domain.interactor.favorite

import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.FavoriteGateway
import dev.olog.domain.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject


class RemoveSongFromFavoriteUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val gateway: FavoriteGateway

) : CompletableUseCaseWithParam<Long>(schedulers) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun buildUseCaseObservable(songId: Long): Completable {
        return gateway.deleteSingle(songId)
    }
}


