package dev.olog.presentation.edit.song

import dev.olog.core.entity.LastFmTrack
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.ImageRetrieverGateway
import dev.olog.core.gateway.base.Id
import dev.olog.core.gateway.track.TrackGateway
import dev.olog.intents.AppConstants
import dev.olog.presentation.PresentationId
import javax.inject.Inject

class EditTrackFragmentPresenter @Inject constructor(
    private val trackGateway: TrackGateway,
    private val lastFmGateway: ImageRetrieverGateway

) {

    fun getSong(mediaId: PresentationId.Track): Song {
        val track = trackGateway.getByParam(mediaId.id)!!
        return track.copy(
            artist = if (track.artist == AppConstants.UNKNOWN) "" else track.artist,
            album = if (track.album == AppConstants.UNKNOWN) "" else track.album
        )
    }

    suspend fun fetchData(id: Id): LastFmTrack? {
        return lastFmGateway.getTrack(id)
    }

}