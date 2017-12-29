package dev.olog.domain.interactor.favorite

import dev.olog.domain.gateway.FavoriteGateway
import javax.inject.Inject

class ToggleLastFavoriteUseCase @Inject constructor(
        private val gateway: FavoriteGateway

) {

    fun execute() {
        gateway.toggleLastFavorite()
    }
}