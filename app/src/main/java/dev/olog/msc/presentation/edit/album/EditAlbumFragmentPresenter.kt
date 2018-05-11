package dev.olog.msc.presentation.edit.album

import android.annotation.SuppressLint
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.interactor.all.GetSongListByParamUseCase
import dev.olog.msc.domain.interactor.last.fm.DeleteLastFmAlbumUseCase
import dev.olog.msc.utils.MediaId
import io.reactivex.Single
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

    @SuppressLint("RxLeakedSubscription")
    fun deleteLastFmEntry(){
        deleteLastFmAlbumUseCase.execute(songList[0].id)
                .subscribe({}, Throwable::printStackTrace)
    }

}