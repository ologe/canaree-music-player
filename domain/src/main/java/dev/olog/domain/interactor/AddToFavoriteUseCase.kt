package dev.olog.domain.interactor

import dev.olog.domain.entity.Favorite
import dev.olog.domain.entity.track.Track
import dev.olog.domain.gateway.FavoriteGateway
import dev.olog.domain.interactor.songlist.GetSongListByParamUseCase
import dev.olog.domain.mediaid.MediaId
import dev.olog.shared.exhaustive
import javax.inject.Inject

class AddToFavoriteUseCase @Inject constructor(
    private val favoriteGateway: FavoriteGateway,
    private val getSongListByParamUseCase: GetSongListByParamUseCase
) {

    suspend operator fun invoke(mediaId: MediaId, type: Favorite.Type) {
        when (mediaId) {
            is MediaId.Category -> handleCategory(mediaId, type)
            is MediaId.Track -> handleTrack(mediaId, type)
        }.exhaustive
    }

    private suspend fun handleTrack(mediaId: MediaId.Track, type: Favorite.Type) {
        favoriteGateway.addSingle(
            type = type,
            trackId = mediaId.id
        )
    }

    private suspend fun handleCategory(mediaId: MediaId.Category, type: Favorite.Type) {
        val ids = getSongListByParamUseCase(mediaId).map(Track::id)
        return favoriteGateway.addGroup(type, ids)
    }

}