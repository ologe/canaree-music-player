package dev.olog.msc.presentation.edit.album

import dev.olog.core.MediaId
import dev.olog.core.entity.track.Album
import dev.olog.core.entity.track.Song
import dev.olog.msc.domain.interactor.all.ObserveSongListByParamUseCase
import dev.olog.msc.domain.interactor.item.GetAlbumUseCase
import dev.olog.msc.domain.interactor.item.GetPodcastAlbumUseCase
import dev.olog.msc.utils.k.extension.get
import io.reactivex.Single
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import javax.inject.Inject

class EditAlbumFragmentPresenter @Inject constructor(
    private val mediaId: MediaId,
    private val getAlbumUseCase: GetAlbumUseCase,
    private val getPodcastAlbumUseCase: GetPodcastAlbumUseCase,
    private val getSongListByParamUseCase: ObserveSongListByParamUseCase

) {

    lateinit var songList: List<Song>
    private lateinit var originalAlbum: DisplayableAlbum

    fun observeAlbum(): Single<DisplayableAlbum> {
        if (mediaId.isPodcastAlbum){
            return observePodcastAlbumInternal()
        }
        return observeAlbumInternal()
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
                tag.get(FieldKey.ARTIST),
                tag.get(FieldKey.ALBUM_ARTIST),
                tag.get(FieldKey.GENRE),
                tag.get(FieldKey.YEAR)
        )
    }

}