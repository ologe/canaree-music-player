package dev.olog.presentation.edit.track

import dev.olog.core.MediaId
import dev.olog.core.entity.LastFmTrack
import dev.olog.core.entity.track.Track
import dev.olog.core.gateway.ImageRetrieverGateway
import dev.olog.core.gateway.base.Id
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.intents.AppConstants
import javax.inject.Inject

class EditTrackFragmentPresenter @Inject constructor(
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
        val album = if (track.album == AppConstants.UNKNOWN) "" else track.album
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