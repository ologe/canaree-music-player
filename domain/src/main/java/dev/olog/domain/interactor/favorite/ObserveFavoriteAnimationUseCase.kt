package dev.olog.domain.interactor.favorite

import dev.olog.domain.entity.favorite.FavoriteState
import dev.olog.domain.gateway.FavoriteGateway
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFavoriteAnimationUseCase @Inject constructor(
    private val gateway: FavoriteGateway

)  {

    operator fun invoke(): Flow<FavoriteState> {
        return gateway.observeToggleFavorite()
    }
}