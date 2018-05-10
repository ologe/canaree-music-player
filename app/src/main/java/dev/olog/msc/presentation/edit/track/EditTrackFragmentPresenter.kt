package dev.olog.msc.presentation.edit.track

import android.annotation.SuppressLint
import com.github.dmstocking.optional.java.util.Optional
import dev.olog.msc.constants.AppConstants
import dev.olog.msc.domain.entity.LastFmTrack
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.interactor.item.GetUneditedSongUseCase
import dev.olog.msc.domain.interactor.last.fm.DeleteLastFmTrackUseCase
import dev.olog.msc.domain.interactor.last.fm.GetLastFmTrackUseCase
import dev.olog.msc.domain.interactor.last.fm.LastFmTrackRequest
import dev.olog.msc.domain.interactor.update.UpdateTrackUseCase
import dev.olog.msc.utils.MediaId
import io.reactivex.Single
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import javax.inject.Inject

class EditTrackFragmentPresenter @Inject constructor(
        private val mediaId: MediaId,
        private val getSongUseCase: GetUneditedSongUseCase,
        private val getLastFmTrackUseCase: GetLastFmTrackUseCase,
        private val updateTrackUseCase: UpdateTrackUseCase,
        private val deleteLastFmTrackUseCase: DeleteLastFmTrackUseCase

) {

    private lateinit var originalSong : Song

    fun getSong(): Single<Song> {
        return getSongUseCase.execute(mediaId)
                .firstOrError()
                .map { it.copy(
                        artist = if (it.artist == AppConstants.UNKNOWN) "" else it.artist,
                        album = if (it.album == AppConstants.UNKNOWN) "" else it.album
                ) }.doOnSuccess { originalSong = it }
    }

    fun getId() = originalSong.id.toInt()
    fun getPath() = originalSong.path

    fun fetchData(): Single<Optional<LastFmTrack?>> {
        return getLastFmTrackUseCase.execute(
                LastFmTrackRequest(originalSong.id, originalSong.title, originalSong.artist, originalSong.album)
        )
    }

    fun updateSong(
            title: String,
            artist: String,
            album: String,
            genre: String,
            year: String,
            disc: String,
            track: String

    ) {
        val file = File(originalSong.path)
        val audioFile = AudioFileIO.read(file)

        val tag = audioFile.tagOrCreateAndSetDefault
        catchNothing { tag.setField(FieldKey.TITLE, title) }
        catchNothing { tag.setField(FieldKey.ARTIST, artist) }
        catchNothing { tag.setField(FieldKey.ALBUM_ARTIST, artist) }
        catchNothing { tag.setField(FieldKey.ALBUM, album) }
        catchNothing { tag.setField(FieldKey.GENRE, genre) }
        catchNothing { tag.setField(FieldKey.YEAR, year) }
        catchNothing { tag.setField(FieldKey.DISC_NO, disc) }
        catchNothing { tag.setField(FieldKey.TRACK, track) }

        audioFile.commit()
    }

    private fun catchNothing(func:() -> Unit){
        try {
            func()
        } catch (ex: Exception){}
    }

    @SuppressLint("RxLeakedSubscription")
    fun deleteLastFmEntry(){
        deleteLastFmTrackUseCase.execute(originalSong.id)
                .subscribe({}, Throwable::printStackTrace)
    }

//    fun updateSong(
//            title: String,
//            artist: String,
//            album: String,
//            genre: String,
//            year: String,
//            disc: String,
//            track: String
//
//    ) {
//
//        val updateWork = updateTrackUseCase.execute(mapOf(
//                UpdateTrackUseCase.PATH to originalSong.path,
//                UpdateTrackUseCase.NEW_TITLE to title,
//                UpdateTrackUseCase.NEW_ARTIST to artist,
//                UpdateTrackUseCase.NEW_ALBUM to album,
//                UpdateTrackUseCase.NEW_GENRE to genre,
//                UpdateTrackUseCase.NEW_YEAR to year,
//                UpdateTrackUseCase.NEW_DISC_NO to disc,
//                UpdateTrackUseCase.NEW_TRACK_NO to track
//
//        ))
//
//        workManager.beginWith(deleteLastFmEntry())
//                .then(updateWork)
//                .enqueue()
//    }
//
//    private fun deleteLastFmEntry(): OneTimeWorkRequest {
//        return deleteLastFmTrackUseCase.execute(mapOf(
//                DeleteLastFmTrackUseCase.TRACK_ID to originalSong.id))
//    }

}