package dev.olog.core.interactor.favorite

import dev.olog.core.entity.favorite.FavoriteEnum
import dev.olog.core.gateway.FavoriteGateway
import dev.olog.core.interactor.base.FlowUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFavoriteAnimationUseCase @Inject constructor(
    private val gateway: FavoriteGateway

) : FlowUseCase<FavoriteEnum>() {

    override fun buildUseCase(): Flow<FavoriteEnum> {
        return gateway.observeToggleFavorite()
    }
}