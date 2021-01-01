package dev.olog.domain.interactor.favorite

import dev.olog.domain.entity.Favorite
import dev.olog.domain.gateway.FavoriteGateway
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFavoriteAnimationUseCase @Inject constructor(
    private val gateway: FavoriteGateway
) {

    operator fun invoke(): Flow<Favorite.State> {
        return gateway.observeToggleFavorite()
    }
}