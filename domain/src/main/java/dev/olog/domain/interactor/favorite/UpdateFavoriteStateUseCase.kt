package dev.olog.domain.interactor.favorite

import dev.olog.domain.entity.Favorite
import dev.olog.domain.gateway.FavoriteGateway
import javax.inject.Inject

class UpdateFavoriteStateUseCase @Inject constructor(
    private val favoriteGateway: FavoriteGateway
) {

    suspend operator fun invoke(param: Favorite) {
        return favoriteGateway.updateFavoriteState(param)
    }
}