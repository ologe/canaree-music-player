package dev.olog.domain.interactor.lastplayed

import dev.olog.domain.MediaId
import dev.olog.domain.MediaIdCategory
import dev.olog.domain.gateway.track.AlbumGateway
import javax.inject.Inject

class InsertLastPlayedAlbumUseCase @Inject constructor(
    private val albumGateway: AlbumGateway

) {

    suspend operator fun invoke(mediaId: MediaId.Category) {
        when (mediaId.category) {
            MediaIdCategory.ALBUMS -> albumGateway.addLastPlayed(mediaId.categoryId.toLong())
            else -> throw IllegalArgumentException("invalid category ${mediaId.category}")
        }
    }

}