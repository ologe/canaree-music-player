package dev.olog.core.interactor.favorite

import dev.olog.core.entity.favorite.FavoriteTrackType
import dev.olog.core.gateway.FavoriteGateway
import javax.inject.Inject

class IsFavoriteSongUseCase @Inject constructor(
    private val gateway: FavoriteGateway
) {

    suspend operator fun invoke(param: Input): Boolean {
        return gateway.isFavorite(param.songId, param.type)
    }

    class Input(
        val songId: Long,
        val type: FavoriteTrackType
    )
}
