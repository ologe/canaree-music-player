package dev.olog.presentation.edit.song

import dev.olog.domain.entity.LastFmTrack
import dev.olog.domain.entity.track.Song
import dev.olog.domain.gateway.ImageRetrieverGateway
import dev.olog.domain.gateway.track.TrackGateway
import dev.olog.intents.AppConstants
import dev.olog.feature.presentation.base.model.PresentationId
import javax.inject.Inject

class EditTrackFragmentPresenter @Inject constructor(
    private val trackGateway: TrackGateway,
    private val lastFmGateway: ImageRetrieverGateway

) {

    fun getSong(mediaId: PresentationId.Track): Song {
        val track = trackGateway.getByParam(mediaId.id.toLong())!!
        return track.copy(
            artist = if (track.artist == AppConstants.UNKNOWN) "" else track.artist,
            album = if (track.album == AppConstants.UNKNOWN) "" else track.album
        )
    }

    suspend fun fetchData(id: Long): LastFmTrack? {
        return lastFmGateway.getTrack(id)
    }

}