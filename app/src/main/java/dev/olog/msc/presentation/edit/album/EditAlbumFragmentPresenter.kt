package dev.olog.msc.presentation.edit.album

import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.interactor.GetSongListByParamUseCase
import dev.olog.msc.utils.MediaId
import io.reactivex.Single
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import javax.inject.Inject

class EditAlbumFragmentPresenter @Inject constructor(
        private val mediaId: MediaId,
        private val getSongListByParamUseCase: GetSongListByParamUseCase

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

        tag.setField(FieldKey.ALBUM, album)
        tag.setField(FieldKey.ARTIST, artist)
        tag.setField(FieldKey.ALBUM_ARTIST, artist)
        tag.setField(FieldKey.GENRE, genre)
        try {
            tag.setField(FieldKey.YEAR, year)
        } catch (ex: Exception){/*year often throws*/}

        audioFile.commit()
    }

}