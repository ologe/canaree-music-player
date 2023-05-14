package dev.olog.presentation.edit.song

import android.provider.MediaStore
import dev.olog.core.MediaId
import dev.olog.core.entity.LastFmTrack
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.ImageRetrieverGateway
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.SongGateway
import javax.inject.Inject

class EditTrackFragmentPresenter @Inject constructor(
    private val songGateway: SongGateway,
    private val podcastGateway: PodcastGateway,
    private val lastFmGateway: ImageRetrieverGateway

) {

    fun getSong(mediaId: MediaId): Song {
        val song = if (mediaId.isPodcast) {
            podcastGateway.getById(mediaId.id)!!
        } else {
            songGateway.getById(mediaId.id)!!
        }
        return song.copy(
            artist = if (song.artist == MediaStore.UNKNOWN_STRING) "" else song.artist,
            album = if (song.album == MediaStore.UNKNOWN_STRING) "" else song.album
        )
    }

    suspend fun fetchData(id: Long): LastFmTrack? {
        return lastFmGateway.getTrack(id)
    }

}