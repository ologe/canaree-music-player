package dev.olog.msc.domain.interactor.favorite

import dev.olog.msc.domain.gateway.FavoriteGateway
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
        private val gateway: FavoriteGateway

) {

    fun execute(songId: Long) {
        gateway.toggleFavorite(songId)
    }
}

