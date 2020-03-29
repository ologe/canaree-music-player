package dev.olog.presentation.edit.artist

import dev.olog.core.entity.track.Artist
import dev.olog.core.gateway.podcast.PodcastAuthorGateway
import dev.olog.core.gateway.track.ArtistGateway
import dev.olog.presentation.PresentationId
import javax.inject.Inject

class EditArtistFragmentPresenter @Inject constructor(
    private val artistGateway: ArtistGateway,
    private val podcastAuthorGateway: PodcastAuthorGateway

) {

    fun getArtist(mediaId: PresentationId.Category): Artist {
        val artist = if (mediaId.isAnyPodcast) {
            podcastAuthorGateway.getByParam(mediaId.categoryId.toLong())!!
        } else {
            artistGateway.getByParam(mediaId.categoryId.toLong())!!
        }
        return Artist(
            id = artist.id,
            name = artist.name,
            albumArtist = artist.albumArtist,
            songs = artist.songs,
            isPodcast = artist.isPodcast
        )
    }

}