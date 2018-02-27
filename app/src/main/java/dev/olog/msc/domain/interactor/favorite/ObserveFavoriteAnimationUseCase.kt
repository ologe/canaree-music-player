package dev.olog.msc.domain.interactor.favorite

import dev.olog.msc.domain.entity.FavoriteEnum
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.FavoriteGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

class ObserveFavoriteAnimationUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val gateway: FavoriteGateway

) : ObservableUseCase<FavoriteEnum>(scheduler) {

    override fun buildUseCaseObservable(): Observable<FavoriteEnum> {
        return gateway.observeToggleFavorite()
    }
}