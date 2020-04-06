package dev.olog.domain.interactor.favorite

import dev.olog.domain.entity.favorite.FavoriteTrackType
import dev.olog.domain.gateway.FavoriteGateway
import javax.inject.Inject

class IsFavoriteSongUseCase @Inject constructor(
    private val gateway: FavoriteGateway
) {

    suspend operator fun invoke(songId: Long, type: FavoriteTrackType): Boolean {
        return gateway.isFavorite(songId, type)
    }
}
