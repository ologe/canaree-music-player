package dev.olog.msc.domain.interactor.favorite

import dev.olog.msc.domain.entity.AnimateFavoriteEntity
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.FavoriteGateway
import dev.olog.msc.domain.interactor.base.FlowableUseCase
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