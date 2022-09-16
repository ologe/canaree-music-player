package dev.olog.core.interactor

import dev.olog.core.MediaId
import dev.olog.core.gateway.FavoriteGateway
import dev.olog.core.interactor.songlist.GetSongListByParamUseCase
import javax.inject.Inject

class AddToFavoriteUseCase @Inject constructor(
    private val favoriteGateway: FavoriteGateway,
    private val getSongListByParamUseCase: GetSongListByParamUseCase

) {

    suspend operator fun invoke(mediaId: MediaId) {
        if (mediaId.isLeaf) {
            val songId = mediaId.leaf!!
            return favoriteGateway.addSingle(songId)
        }

        val ids = getSongListByParamUseCase(mediaId).map { it.id }
        return favoriteGateway.addGroup(ids)
    }

}