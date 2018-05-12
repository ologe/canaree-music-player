package dev.olog.msc.presentation.edit.album

import dev.olog.msc.domain.entity.Album
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.interactor.all.GetSongListByParamUseCase
import dev.olog.msc.domain.interactor.item.GetAlbumUseCase
import dev.olog.msc.utils.MediaId
import io.reactivex.Single
import javax.inject.Inject

class EditAlbumFragmentPresenter @Inject constructor(
        private val mediaId: MediaId,
        private val getAlbumUseCase: GetAlbumUseCase,
        private val getSongListByParamUseCase: GetSongListByParamUseCase

) {

    lateinit var songList: List<Song>
    private lateinit var originalAlbum: Album

    fun observeAlbum(): Single<Pair<Album, String>> {
        return getAlbumUseCase.execute(mediaId)
                .flatMap { original ->
                    getSongListByParamUseCase.execute(mediaId)
                            .map { original to it[0].path }
                }
                .firstOrError()
                .doOnSuccess { originalAlbum = it.first }
    }

    fun getSongList(): Single<List<Song>> {
        return getSongListByParamUseCase.execute(mediaId)
                .firstOrError()
                .doOnSuccess { songList = it }
    }

    fun getAlbum(): Album = originalAlbum

}