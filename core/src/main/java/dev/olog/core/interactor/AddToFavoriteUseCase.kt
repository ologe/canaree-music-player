package dev.olog.core.interactor

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.favorite.FavoriteType
import dev.olog.core.gateway.FavoriteGateway
import dev.olog.core.interactor.songlist.GetSongListByParamUseCase
import javax.inject.Inject

class AddToFavoriteUseCase @Inject constructor(
    private val favoriteGateway: FavoriteGateway,
    private val getSongListByParamUseCase: GetSongListByParamUseCase

) {

    suspend operator fun invoke(mediaId: MediaId) {
        val type = if (mediaId.isPodcast) FavoriteType.PODCAST else FavoriteType.TRACK
        when (mediaId.category) {
            MediaIdCategory.SONGS -> {
                val songId = mediaId.id
                favoriteGateway.addSingle(type, songId)
            }
            else -> {
                val ids = getSongListByParamUseCase(mediaId).map { it.id }
                favoriteGateway.addGroup(type, ids)
            }
        }
    }

}