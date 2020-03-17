package dev.olog.core.interactor.favorite

import dev.olog.core.entity.favorite.FavoriteItemState
import dev.olog.core.gateway.FavoriteGateway
import javax.inject.Inject

class UpdateFavoriteStateUseCase @Inject constructor(
    private val favoriteGateway: FavoriteGateway
) {

    suspend operator fun invoke(param: FavoriteItemState) {
        return favoriteGateway.updateFavoriteState(param)
    }
}