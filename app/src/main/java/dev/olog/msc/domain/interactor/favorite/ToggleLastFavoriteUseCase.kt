package dev.olog.msc.domain.interactor.favorite

import dev.olog.msc.domain.gateway.FavoriteGateway
import javax.inject.Inject

class ToggleLastFavoriteUseCase @Inject constructor(
        private val gateway: FavoriteGateway

) {

    fun execute() {
        gateway.toggleLastFavorite()
    }
}