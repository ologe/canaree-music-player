package dev.olog.msc.presentation.edit.album

import android.annotation.SuppressLint
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.interactor.GetSongListByParamUseCase
import dev.olog.msc.domain.interactor.last.fm.DeleteLastFmAlbumUseCase
import dev.olog.msc.utils.MediaId
import io.reactivex.Single
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import javax.inject.Inject

class EditAlbumFragmentPresenter @Inject constructor(
        private val mediaId: MediaId,
        private val getSongListByParamUseCase: GetSongListByParamUseCase,
        private val deleteLastFmAlbumUseCase: DeleteLastFmAlbumUseCase

) {

    lateinit var songList: List<Song>

    fun getSongList(): Single<List<Song>> {
        return getSongListByParamUseCase.execute(mediaId)
                .firstOrError()
                .doOnSuccess { songList = it }
    }

    fun updateSongList(
            album: String,
            artist: String,
            genre: String,
            year: String) {

        for (song in songList) {
            updateSong(song.path, album, artist, genre, year)
        }

    }

    private fun updateSong(
            path: String,
            album: String,
            artist: String,
            genre: String,
            year: String

    ){
        val file = File(path)
        val audioFile = AudioFileIO.read(file)
        val tag = audioFile.tagOrCreateAndSetDefault

        catchNothing { tag.setField(FieldKey.ALBUM, album) }
        catchNothing { tag.setField(FieldKey.ARTIST, artist) }
        catchNothing { tag.setField(FieldKey.ALBUM_ARTIST, artist) }
        catchNothing { tag.setField(FieldKey.GENRE, genre) }
        catchNothing { tag.setField(FieldKey.YEAR, year) }

        audioFile.commit()
    }

    private fun catchNothing(func:() -> Unit){
        try {
            func()
        } catch (ex: Exception){}
    }

    @SuppressLint("RxLeakedSubscription")
    fun deleteLastFmEntry(){
        deleteLastFmAlbumUseCase.execute(songList[0].id)
                .subscribe({}, Throwable::printStackTrace)
    }

}