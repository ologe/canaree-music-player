package dev.olog.domain.interactor.favorite

import dev.olog.domain.entity.Favorite
import dev.olog.domain.gateway.FavoriteGateway
import javax.inject.Inject

class IsFavoriteSongUseCase @Inject constructor(
    private val gateway: FavoriteGateway
) {

    suspend operator fun invoke(trackId: Long, type: Favorite.Type): Boolean {
        return gateway.isFavorite(type, trackId)
    }

}
