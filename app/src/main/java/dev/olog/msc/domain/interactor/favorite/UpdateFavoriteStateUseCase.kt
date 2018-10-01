package dev.olog.msc.domain.interactor.favorite

import dev.olog.msc.domain.entity.FavoriteStateEntity
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.FavoriteGateway
import dev.olog.msc.domain.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class UpdateFavoriteStateUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val favoriteGateway: FavoriteGateway

) : CompletableUseCaseWithParam<FavoriteStateEntity>(schedulers) {

    override fun buildUseCaseObservable(param: FavoriteStateEntity): Completable {
        return Completable.fromCallable {
            favoriteGateway.updateFavoriteState(param.favoriteType, param)
        }
    }
}