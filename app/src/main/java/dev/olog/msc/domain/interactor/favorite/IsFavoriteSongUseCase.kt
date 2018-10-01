package dev.olog.msc.domain.interactor.favorite

import dev.olog.msc.domain.entity.FavoriteType
import dev.olog.msc.domain.executors.IoScheduler
import dev.olog.msc.domain.gateway.FavoriteGateway
import dev.olog.msc.domain.interactor.base.SingleUseCaseWithParam
import io.reactivex.Single
import javax.inject.Inject

class IsFavoriteSongUseCase @Inject constructor(
        schedulers: IoScheduler,
        private val gateway: FavoriteGateway

) : SingleUseCaseWithParam<Boolean, IsFavoriteSongUseCase.Input>(schedulers) {

    override fun buildUseCaseObservable(param: IsFavoriteSongUseCase.Input): Single<Boolean> {
        return gateway.isFavorite(param.type, param.songId)
    }

    class Input(
            val songId: Long,
            val type: FavoriteType
    )
}
