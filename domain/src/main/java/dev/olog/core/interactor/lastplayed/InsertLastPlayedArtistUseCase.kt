package dev.olog.core.interactor.lastplayed

import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.gateway.podcast.PodcastAuthorGateway
import dev.olog.core.gateway.track.ArtistGateway
import javax.inject.Inject

class InsertLastPlayedArtistUseCase @Inject constructor(
    private val artistGateway: ArtistGateway,
    private val podcastGateway: PodcastAuthorGateway

) {

    suspend operator fun invoke(mediaId: MediaId.Category) {
        when (mediaId.category) {
            MediaIdCategory.ARTISTS -> artistGateway.addLastPlayed(mediaId.categoryId.toLong())
            MediaIdCategory.PODCASTS_AUTHORS -> podcastGateway.addLastPlayed(mediaId.categoryId.toLong())
            else -> throw IllegalArgumentException("invalid category ${mediaId.category}")
        }
    }
}