package dev.olog.msc.presentation.edit.artist

import dev.olog.msc.domain.entity.Artist
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.interactor.GetSongListByParamUseCase
import dev.olog.msc.domain.interactor.detail.item.GetArtistUseCase
import dev.olog.msc.utils.MediaId
import io.reactivex.Single
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import javax.inject.Inject

class EditArtistFragmentPresenter @Inject constructor(
        private val mediaId: MediaId,
        private val getArtistUseCase: GetArtistUseCase,
        private val getSongListByParamUseCase: GetSongListByParamUseCase

) {

    private lateinit var originalArtist: Artist
    lateinit var songList: List<Song>

    fun getArtist(): Single<Artist> {
        return getArtistUseCase.execute(mediaId)
                .firstOrError()
                .doOnSuccess { originalArtist = it }
    }

    fun getSongList(): Single<List<Song>> {
        return getSongListByParamUseCase.execute(mediaId)
                .firstOrError()
                .doOnSuccess { songList = it }
    }

    fun updateSongList(artist: String) {
        for (song in songList) {
            updateSong(song.path, artist)
        }
    }

    private fun updateSong(path: String, artist: String){
        val file = File(path)
        val audioFile = AudioFileIO.read(file)
        val tag = audioFile.tagOrCreateAndSetDefault

        tag.setField(FieldKey.ARTIST, artist)
        tag.setField(FieldKey.ALBUM_ARTIST, artist)
        audioFile.commit()
    }

}