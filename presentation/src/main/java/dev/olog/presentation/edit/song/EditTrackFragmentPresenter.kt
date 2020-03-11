package dev.olog.presentation.edit.song

import dev.olog.core.entity.LastFmTrack
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.ImageRetrieverGateway
import dev.olog.core.gateway.base.Id
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.intents.AppConstants
import dev.olog.presentation.PresentationId
import javax.inject.Inject

class EditTrackFragmentPresenter @Inject constructor(
    private val songGateway: SongGateway,
    private val podcastGateway: PodcastGateway,
    private val lastFmGateway: ImageRetrieverGateway

) {

    fun getSong(mediaId: PresentationId.Track): Song {
        val song = if (mediaId.isAnyPodcast) {
            podcastGateway.getByParam(mediaId.id)!!
        } else {
            songGateway.getByParam(mediaId.id)!!
        }
        return song.copy(
            artist = if (song.artist == AppConstants.UNKNOWN) "" else song.artist,
            album = if (song.album == AppConstants.UNKNOWN) "" else song.album
        )
    }

    suspend fun fetchData(id: Id): LastFmTrack? {
        return lastFmGateway.getTrack(id)
    }

}