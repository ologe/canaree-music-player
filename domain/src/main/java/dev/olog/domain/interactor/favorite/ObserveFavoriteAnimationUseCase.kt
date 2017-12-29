package dev.olog.domain.interactor.favorite

import dev.olog.domain.entity.AnimateFavoriteEntity
import dev.olog.domain.executor.IoScheduler
import dev.olog.domain.gateway.FavoriteGateway
import dev.olog.domain.interactor.base.FlowableUseCase
import io.reactivex.Flowable
import javax.inject.Inject

class ObserveFavoriteAnimationUseCase @Inject constructor(
        scheduler: IoScheduler,
        private val gateway: FavoriteGateway

) : FlowableUseCase<AnimateFavoriteEntity>(scheduler) {

    override fun buildUseCaseObservable(): Flowable<AnimateFavoriteEntity> {
        return gateway.observeToggleFavorite()
    }
}