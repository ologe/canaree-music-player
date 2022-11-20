package dev.olog.presentation.edit.artist

import dev.olog.core.MediaId
import dev.olog.core.entity.track.Artist
import dev.olog.core.gateway.podcast.PodcastArtistGateway
import dev.olog.core.gateway.track.ArtistGateway
import javax.inject.Inject

class EditArtistFragmentPresenter @Inject constructor(
    private val artistGateway: ArtistGateway,
    private val podcastArtistGateway: PodcastArtistGateway,
) {

    fun getArtist(mediaId: MediaId): Artist {
        val artist = if (mediaId.isPodcastArtist) {
            podcastArtistGateway.getByParam(mediaId.categoryId)!!
        } else {
            artistGateway.getByParam(mediaId.categoryId)!!
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