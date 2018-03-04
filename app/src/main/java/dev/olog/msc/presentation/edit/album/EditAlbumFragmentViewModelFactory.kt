package dev.olog.msc.presentation.edit.album

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.msc.domain.gateway.LastFmGateway
import dev.olog.msc.domain.interactor.GetSongListByParamUseCase
import dev.olog.msc.domain.interactor.detail.item.GetAlbumUseCase
import dev.olog.msc.presentation.NetworkConnectionPublisher
import dev.olog.msc.utils.MediaId
import javax.inject.Inject

class EditAlbumFragmentViewModelFactory @Inject constructor(
        private val application: Application,
        private val mediaId: MediaId,
        private val getAlbumUseCase: GetAlbumUseCase,
        private val getSongListByParamUseCase: GetSongListByParamUseCase,
        private val connectionPublisher: NetworkConnectionPublisher,
        private val lastFm: LastFmGateway

) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EditAlbumFragmentViewModel(
                application, mediaId,
                getAlbumUseCase,
                getSongListByParamUseCase,
                connectionPublisher,
                lastFm
        ) as T
    }
}