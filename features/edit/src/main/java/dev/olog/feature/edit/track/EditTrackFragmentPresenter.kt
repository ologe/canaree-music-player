package dev.olog.feature.edit.track

import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.entity.LastFmTrack
import dev.olog.domain.entity.track.Track
import dev.olog.domain.gateway.ImageRetrieverGateway
import dev.olog.domain.gateway.base.Id
import dev.olog.domain.gateway.podcast.PodcastGateway
import dev.olog.domain.gateway.track.SongGateway
import javax.inject.Inject

internal class EditTrackFragmentPresenter @Inject constructor(
    private val songGateway: SongGateway,
    private val podcastGateway: PodcastGateway,
    private val lastFmGateway: ImageRetrieverGateway
) {

    suspend fun getSong(mediaId: MediaId): Track {
        val track = if (mediaId.isPodcast) {
            podcastGateway.getByParam(mediaId.leaf!!)!!
        } else {
            songGateway.getByParam(mediaId.leaf!!)!!
        }
        val artist = if (track.hasUnknownArtist) "" else track.artist
        val album = if (track.hasUnknownAlbum) "" else track.album
        return when (track) {
            is Track.Song -> track.copy(
                artist = artist,
                album = album,
            )
            is Track.PlaylistSong -> track.copy(
                artist = artist,
                album = album,
            )
        }
    }

    suspend fun fetchData(id: Id): LastFmTrack? {
        return lastFmGateway.getTrack(id)
    }

}