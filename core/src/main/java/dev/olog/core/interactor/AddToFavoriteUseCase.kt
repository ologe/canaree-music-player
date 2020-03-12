package dev.olog.core.interactor

import dev.olog.core.MediaId
import dev.olog.core.MediaId.Category
import dev.olog.core.MediaId.Track
import dev.olog.core.entity.favorite.FavoriteTrackType
import dev.olog.core.gateway.FavoriteGateway
import dev.olog.core.interactor.songlist.GetSongListByParamUseCase
import javax.inject.Inject

class AddToFavoriteUseCase @Inject constructor(
    private val favoriteGateway: FavoriteGateway,
    private val getSongListByParamUseCase: GetSongListByParamUseCase

) {

    suspend operator fun invoke(input: Input) {
        return when (val mediaId = input.mediaId) {
            is Track -> favoriteGateway.addSingle(input.type, mediaId.id)
            is Category -> {
                val ids = getSongListByParamUseCase(mediaId).map { it.id }
                favoriteGateway.addGroup(input.type, ids)
            }
        }
    }

    class Input(
        val mediaId: MediaId,
        val type: FavoriteTrackType
    )

}