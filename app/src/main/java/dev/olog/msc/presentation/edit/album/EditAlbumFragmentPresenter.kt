package dev.olog.msc.presentation.edit.album

import dev.olog.msc.constants.AppConstants
import dev.olog.msc.domain.entity.Album
import dev.olog.msc.domain.entity.LastFmAlbum
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.interactor.GetSongListByParamUseCase
import dev.olog.msc.domain.interactor.detail.item.GetAlbumUseCase
import dev.olog.msc.domain.interactor.image.album.DeleteAlbumImageUseCase
import dev.olog.msc.domain.interactor.image.album.InsertAlbumImageUseCase
import dev.olog.msc.domain.interactor.last.fm.GetLastFmAlbumUseCase
import dev.olog.msc.domain.interactor.last.fm.LastFmAlbumRequest
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.img.ImagesFolderUtils
import io.reactivex.Single
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import javax.inject.Inject

class EditAlbumFragmentPresenter @Inject constructor(
        private val mediaId: MediaId,
        private val getAlbumUseCase: GetAlbumUseCase,
        private val getSongListByParamUseCase: GetSongListByParamUseCase,
        private val getLastFmAlbumUseCase: GetLastFmAlbumUseCase,
        private val insertImageUseCase: InsertAlbumImageUseCase,
        private val deleteImageUseCase: DeleteAlbumImageUseCase

) {

    private lateinit var originalAlbum: Album
    lateinit var songList: List<Song>

    fun getAlbum(): Single<Album> {
        return getAlbumUseCase.execute(mediaId)
                .firstOrError()
                .map {it.copy(
                        artist = if (it.artist == AppConstants.UNKNOWN) "" else it.artist
                ) }.doOnSuccess { originalAlbum = it }
    }

    fun getSongList(): Single<List<Song>> {
        return getSongListByParamUseCase.execute(mediaId)
                .firstOrError()
                .doOnSuccess { songList = it }
    }

    fun getAlbumId() = originalAlbum.id.toInt()

    fun getOriginalImage() = ImagesFolderUtils.forAlbum(originalAlbum.id)

    fun fetchData(): Single<LastFmAlbum> {
        return getLastFmAlbumUseCase.execute(
                LastFmAlbumRequest(originalAlbum.id, originalAlbum.title, originalAlbum.artist)
        )
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
        } catch (ex: Exception){/*year ofter throws*/}

        audioFile.commit()
    }

    fun updateUsedImage(image: String){
        if (image == ImagesFolderUtils.forAlbum(originalAlbum.id)){
            deleteImageUseCase.execute(originalAlbum)
                    .subscribe({}, Throwable::printStackTrace)
        } else {
            insertImageUseCase.execute(originalAlbum to image)
                    .subscribe({}, Throwable::printStackTrace)
        }
    }

}