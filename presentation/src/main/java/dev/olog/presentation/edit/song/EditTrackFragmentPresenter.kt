package dev.olog.presentation.edit.song

import dev.olog.core.entity.LastFmTrack
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.presentation.utils.get
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import javax.inject.Inject

class EditTrackFragmentPresenter @Inject constructor(
    private val songGateway: SongGateway,
    private val podcastGateway: PodcastGateway

) {

    fun getSong(): DisplayableSong {
        TODO()
//        if (mediaId.isPodcast) {
//            return observePodcastInternal()
//        }
//        return observeSongInternal()
    }

    private fun observeSongInternal(): DisplayableSong {
        TODO()
//        val song = songGateway.getByParam(mediaId.leaf!!)!!
//        val displayableSong = song.copy(
//            artist = if (song.artist == AppConstants.UNKNOWN) "" else song.artist,
//            album = if (song.album == AppConstants.UNKNOWN) "" else song.album
//        ).toDisplayableSong()
//
//        return displayableSong
    }

    private fun observePodcastInternal(): DisplayableSong {
        TODO()
//        val song = podcastGateway.getByParam(mediaId.leaf!!)!!
//        val displayableSong = song.copy(
//            artist = if (song.artist == AppConstants.UNKNOWN) "" else song.artist,
//            album = if (song.album == AppConstants.UNKNOWN) "" else song.album
//        ).toDisplayableSong()
//
//        return displayableSong
    }

    fun fetchData(): LastFmTrack? {
        TODO()
//        return getLastFmTrackUseCase.execute(
//                LastFmTrackRequest(originalSong.id, originalSong.title, originalSong.artist, originalSong.album)
//        )
    }

    private fun Song.toDisplayableSong(): DisplayableSong {
        val file = File(path)
        val audioFile = AudioFileIO.read(file)
        val audioHeader = audioFile.audioHeader
        val tag = audioFile.tagOrCreateAndSetDefault

        return DisplayableSong(
            this.id,
            this.artistId,
            this.albumId,
            this.title,
            tag.get(FieldKey.ARTIST),
            tag.get(FieldKey.ALBUM_ARTIST),
            album,
            tag.get(FieldKey.GENRE),
            tag.get(FieldKey.YEAR),
            tag.get(FieldKey.DISC_NO),
            tag.get(FieldKey.TRACK),
            this.path,
            audioHeader.bitRate + " kb/s",
            audioHeader.format,
            audioHeader.sampleRate + " Hz",
            false
        )
    }

}