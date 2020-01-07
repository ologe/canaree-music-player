package dev.olog.core.interactor.favorite

import dev.olog.core.entity.favorite.FavoriteTrackType
import dev.olog.core.gateway.FavoriteGateway
import javax.inject.Inject

class IsFavoriteSongUseCase @Inject constructor(
    private val gateway: FavoriteGateway
) {

    suspend operator fun invoke(songId: Long, type: FavoriteTrackType): Boolean {
        return gateway.isFavorite(songId, type)
    }
}
