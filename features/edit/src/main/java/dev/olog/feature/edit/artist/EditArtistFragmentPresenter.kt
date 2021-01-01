package dev.olog.feature.edit.artist

import dev.olog.domain.entity.LastFmArtist
import dev.olog.domain.entity.track.Artist
import dev.olog.domain.gateway.ImageRetrieverGateway
import dev.olog.domain.gateway.base.Id
import dev.olog.domain.gateway.podcast.PodcastArtistGateway
import dev.olog.domain.gateway.track.ArtistGateway
import dev.olog.domain.mediaid.MediaId
import javax.inject.Inject

internal class EditArtistFragmentPresenter @Inject constructor(
    private val artistGateway: ArtistGateway,
    private val podcastArtistGateway: PodcastArtistGateway,
    private val lastFmGateway: ImageRetrieverGateway

) {

    suspend fun getArtist(mediaId: MediaId.Category): Artist {
        val artist = if (mediaId.isPodcastArtist) {
            podcastArtistGateway.getByParam(mediaId.categoryValue.toLong())!!
        } else {
            artistGateway.getByParam(mediaId.categoryValue.toLong())!!
        }
        return Artist(
            id = artist.id,
            name = artist.name,
            albumArtist = artist.albumArtist,
            songs = artist.songs,
            isPodcast = artist.isPodcast
        )
    }

    suspend fun fetchData(id: Id): LastFmArtist? {
        return lastFmGateway.getArtist(id)
    }

}