package dev.olog.core.favorite

import dev.olog.core.favorite.FavoriteEnum
import dev.olog.core.favorite.FavoriteGateway
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFavoriteAnimationUseCase @Inject constructor(
    private val gateway: FavoriteGateway,
) {

    operator fun invoke(): Flow<FavoriteEnum> {
        return gateway.observeToggleFavorite()
    }
}