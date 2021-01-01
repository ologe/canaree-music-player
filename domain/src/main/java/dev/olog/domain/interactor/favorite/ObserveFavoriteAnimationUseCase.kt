package dev.olog.domain.interactor.favorite

import dev.olog.domain.entity.favorite.FavoriteEnum
import dev.olog.domain.gateway.FavoriteGateway
import dev.olog.domain.interactor.base.FlowUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFavoriteAnimationUseCase @Inject constructor(
    private val gateway: FavoriteGateway

) : FlowUseCase<FavoriteEnum>() {

    override fun buildUseCase(): Flow<FavoriteEnum> {
        return gateway.observeToggleFavorite()
    }
}