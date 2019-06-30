package dev.olog.core.interactor

import dev.olog.core.entity.favorite.FavoriteStateEntity
import dev.olog.core.executor.IoScheduler
import dev.olog.core.gateway.FavoriteGateway
import dev.olog.core.interactor.base.CompletableUseCaseWithParam
import io.reactivex.Completable
import javax.inject.Inject

class UpdateFavoriteStateUseCase @Inject constructor(
    schedulers: IoScheduler,
    private val favoriteGateway: FavoriteGateway

) : CompletableUseCaseWithParam<FavoriteStateEntity>(schedulers) {

    override fun buildUseCaseObservable(param: FavoriteStateEntity): Completable {
        return Completable.fromCallable {
            favoriteGateway.updateFavoriteState(param)
        }
    }
}