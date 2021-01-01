package dev.olog.domain.interactor.favorite

import dev.olog.domain.entity.Favorite
import dev.olog.domain.gateway.FavoriteGateway
import dev.olog.domain.interactor.base.FlowUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFavoriteAnimationUseCase @Inject constructor(
    private val gateway: FavoriteGateway
) : FlowUseCase<Favorite.State>() {

    override fun buildUseCase(): Flow<Favorite.State> {
        return gateway.observeToggleFavorite()
    }
}