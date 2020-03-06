package dev.olog.core.interactor.lastplayed

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.gateway.track.AlbumGateway
import javax.inject.Inject

class InsertLastPlayedAlbumUseCase @Inject constructor(
    private val albumGateway: AlbumGateway

) {

    suspend operator fun invoke(mediaId: MediaId) {
        when (mediaId.category) {
            MediaIdCategory.ALBUMS -> albumGateway.addLastPlayed(mediaId.categoryId)
            else -> throw IllegalArgumentException("invalid category ${mediaId.category}")
        }
    }

}