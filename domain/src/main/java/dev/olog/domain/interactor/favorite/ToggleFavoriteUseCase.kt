package dev.olog.domain.interactor.favorite

import dev.olog.domain.gateway.FavoriteGateway
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
        private val gateway: FavoriteGateway

) {

    fun execute(songId: Long) {
        gateway.toggleFavorite(songId)
    }
}

