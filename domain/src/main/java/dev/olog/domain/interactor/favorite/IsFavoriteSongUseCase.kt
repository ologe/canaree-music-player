package dev.olog.domain.interactor.favorite

import dev.olog.domain.entity.favorite.FavoriteType
import dev.olog.domain.gateway.FavoriteGateway
import javax.inject.Inject

class IsFavoriteSongUseCase @Inject constructor(
    private val gateway: FavoriteGateway
) {

    suspend operator fun invoke(param: Input): Boolean {
        return gateway.isFavorite(param.type, param.songId)
    }

    class Input(
        val songId: Long,
        val type: FavoriteType
    )
}
