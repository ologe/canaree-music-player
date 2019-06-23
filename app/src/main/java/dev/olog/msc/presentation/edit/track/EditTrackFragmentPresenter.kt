package dev.olog.msc.presentation.edit.track

import com.github.dmstocking.optional.java.util.Optional
import dev.olog.presentation.AppConstants
import dev.olog.core.entity.LastFmTrack
import dev.olog.core.entity.podcast.Podcast
import dev.olog.core.entity.track.Song
import dev.olog.msc.domain.gateway.UsedImageGateway
import dev.olog.msc.domain.interactor.item.GetPodcastUseCase
import dev.olog.msc.domain.interactor.item.GetUneditedSongUseCase
import dev.olog.msc.domain.interactor.last.fm.GetLastFmTrackUseCase
import dev.olog.msc.domain.interactor.last.fm.LastFmTrackRequest
import dev.olog.core.MediaId
import dev.olog.msc.utils.k.extension.get
import io.reactivex.Single
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import javax.inject.Inject

class EditTrackFragmentPresenter @Inject constructor(
    private val mediaId: MediaId,
    private val getSongUseCase: GetUneditedSongUseCase,
    private val getPodcastUseCase: GetPodcastUseCase,
    private val getLastFmTrackUseCase: GetLastFmTrackUseCase,
    private val usedImageGateway: UsedImageGateway

) {

    private lateinit var originalSong : DisplayableSong

    fun observeSong(): Single<DisplayableSong> {
        if (mediaId.isPodcast){
            return observePodcastInternal()
        }
        return observeSongInternal()
    }

    private fun observeSongInternal(): Single<DisplayableSong> {
        return getSongUseCase.execute(mediaId)
                .firstOrError()
                .map { it.copy(
                        artist = if (it.artist == AppConstants.UNKNOWN) "" else it.artist,
                        album = if (it.album == AppConstants.UNKNOWN) "" else it.album
                ) }
                .map { it.toDisplayableSong() }
                .doOnSuccess {
//                    val usedImage = usedImageGateway.getForTrack(it.id)
//                            ?: usedImageGateway.getForAlbum(it.albumId)
//                            ?: it.image
//                    originalSong = it.copy(image = usedImage) TODO
                }
    }

    private fun observePodcastInternal(): Single<DisplayableSong> {
        return getPodcastUseCase.execute(mediaId)
                .firstOrError()
                .map { it.copy(
                        artist = if (it.artist == AppConstants.UNKNOWN) "" else it.artist,
                        album = if (it.album == AppConstants.UNKNOWN) "" else it.album
                ) }
                .map { it.toDisplayableSong() }
                .doOnSuccess {
//                    val usedImage = usedImageGateway.getForTrack(it.id)
//                            ?: usedImageGateway.getForAlbum(it.albumId)
//                            ?: it.image
//                    originalSong = it.copy(image = usedImage) TODO
                }
    }

    fun fetchData(): Single<Optional<LastFmTrack?>> {
        return getLastFmTrackUseCase.execute(
                LastFmTrackRequest(originalSong.id, originalSong.title, originalSong.artist, originalSong.album)
        )
    }

    fun getSong(): DisplayableSong = originalSong

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
                audioHeader.sampleRate +  " Hz",
                false
        )
    }

    private fun Podcast.toDisplayableSong(): DisplayableSong {
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
                audioHeader.sampleRate +  " Hz",
                true
        )
    }

}