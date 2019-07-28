package dev.olog.presentation.edit.album

import dev.olog.core.entity.track.Album
import dev.olog.core.entity.track.Song
import dev.olog.core.interactor.songlist.ObserveSongListByParamUseCase
import dev.olog.presentation.utils.safeGet
import io.reactivex.Single
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import javax.inject.Inject

class EditAlbumFragmentPresenter @Inject constructor(
//    private val mediaId: MediaId,
    private val getSongListByParamUseCase: ObserveSongListByParamUseCase

) {

    lateinit var songList: List<Song>
    private lateinit var originalAlbum: DisplayableAlbum

    fun observeAlbum(): Single<DisplayableAlbum> {
        TODO()
//        if (mediaId.isPodcastAlbum){
//            return observePodcastAlbumInternal()
//        }
//        return observeAlbumInternal()
    }

    private fun observeAlbumInternal(): Single<DisplayableAlbum>{
        TODO()
//        return getAlbumUseCase.execute(mediaId)
//                .flatMap { original ->
//                    getSongListByParamUseCase.execute(mediaId)
//                            .map { original.toDisplayableAlbum(it[0].path)  }
//                }
//                .firstOrError()
//                .doOnSuccess { originalAlbum = it }
    }

    private fun observePodcastAlbumInternal(): Single<DisplayableAlbum>{
        TODO()
//        return getPodcastAlbumUseCase.execute(mediaId)
//                .flatMap { original ->
//                    getSongListByParamUseCase.execute(mediaId)
//                            .map { original.toDisplayableAlbum(it[0].path)}
//                }
//                .firstOrError()
//                .doOnSuccess { originalAlbum = it }
    }

    fun getSongList(): Single<List<Song>> {
        TODO()
//        return getSongListByParamUseCase.execute(mediaId)
//                .firstOrError()
//                .doOnSuccess { songList = it }
    }

    fun getAlbum(): DisplayableAlbum = originalAlbum

    private fun Album.toDisplayableAlbum(path: String): DisplayableAlbum {
        val file = File(path)
        val audioFile = AudioFileIO.read(file)
        val tag = audioFile.tagOrCreateAndSetDefault

        return DisplayableAlbum(
            this.id,
            this.title,
            tag.safeGet(FieldKey.ARTIST),
            tag.safeGet(FieldKey.ALBUM_ARTIST),
            tag.safeGet(FieldKey.GENRE),
            tag.safeGet(FieldKey.YEAR)
        )
    }

}