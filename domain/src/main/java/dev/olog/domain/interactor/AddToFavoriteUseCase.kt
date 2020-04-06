package dev.olog.domain.interactor

import dev.olog.domain.MediaId
import dev.olog.domain.MediaId.Category
import dev.olog.domain.MediaId.Track
import dev.olog.domain.entity.favorite.FavoriteTrackType
import dev.olog.domain.gateway.FavoriteGateway
import dev.olog.domain.interactor.songlist.GetSongListByParamUseCase
import javax.inject.Inject

class AddToFavoriteUseCase @Inject constructor(
    private val favoriteGateway: FavoriteGateway,
    private val getSongListByParamUseCase: GetSongListByParamUseCase

) {

    suspend operator fun invoke(input: Input) {
        return when (val mediaId = input.mediaId) {
            is Track -> favoriteGateway.addSingle(input.type, mediaId.id.toLong())
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