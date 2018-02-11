package dev.olog.msc.domain.interactor.favorite

import dev.olog.msc.domain.entity.AnimateFavoriteEntity
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.FavoriteGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

class ObserveFavoriteAnimationUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val gateway: FavoriteGateway

) : ObservableUseCase<AnimateFavoriteEntity>(scheduler) {

    override fun buildUseCaseObservable(): Observable<AnimateFavoriteEntity> {
        return gateway.observeToggleFavorite()
    }
}