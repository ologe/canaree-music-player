package dev.olog.feature.edit.track

import android.provider.MediaStore
import dev.olog.domain.entity.LastFmTrack
import dev.olog.domain.entity.track.Song
import dev.olog.domain.gateway.ImageRetrieverGateway
import dev.olog.domain.gateway.track.TrackGateway
import dev.olog.feature.presentation.base.model.PresentationId
import javax.inject.Inject

class EditTrackFragmentPresenter @Inject constructor(
    private val trackGateway: TrackGateway,
    private val lastFmGateway: ImageRetrieverGateway

) {

    fun getSong(mediaId: PresentationId.Track): Song {
        val track = trackGateway.getByParam(mediaId.id.toLong())!!
        return track.copy(
            artist = if (track.artist == MediaStore.UNKNOWN_STRING) "" else track.artist,
            album = if (track.album == MediaStore.UNKNOWN_STRING) "" else track.album
        )
    }

    suspend fun fetchData(id: Long): LastFmTrack? {
        return lastFmGateway.getTrack(id)
    }

}