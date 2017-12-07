package dev.olog.domain.interactor.favorite

import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.FavoriteGateway
import dev.olog.domain.interactor.base.SingleUseCaseWithParam
import io.reactivex.Single
import javax.inject.Inject


class RemoveSongFromFavoriteUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val gateway: FavoriteGateway

) : SingleUseCaseWithParam<String, Long>(schedulers) {

    override fun buildUseCaseObservable(songId: Long): Single<String> {
        return gateway.deleteSingle(songId)
    }
}


