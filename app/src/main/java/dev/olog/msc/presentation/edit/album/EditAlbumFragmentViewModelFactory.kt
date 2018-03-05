package dev.olog.msc.presentation.edit.album

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.msc.domain.interactor.GetSongListByParamUseCase
import dev.olog.msc.domain.interactor.detail.item.GetAlbumUseCase
import dev.olog.msc.domain.interactor.last.fm.GetLastFmAlbumUseCase
import dev.olog.msc.presentation.NetworkConnectionPublisher
import dev.olog.msc.utils.MediaId
import javax.inject.Inject

class EditAlbumFragmentViewModelFactory @Inject constructor(
        private val application: Application,
        private val mediaId: MediaId,
        private val getAlbumUseCase: GetAlbumUseCase,
        private val getSongListByParamUseCase: GetSongListByParamUseCase,
        private val connectionPublisher: NetworkConnectionPublisher,
        private val getLastFmAlbumUseCase: GetLastFmAlbumUseCase

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EditAlbumFragmentViewModel(
                application, mediaId,
                getAlbumUseCase,
                getSongListByParamUseCase,
                connectionPublisher,
                getLastFmAlbumUseCase
        ) as T
    }
}