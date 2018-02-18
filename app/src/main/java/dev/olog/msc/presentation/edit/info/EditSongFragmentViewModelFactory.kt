package dev.olog.msc.presentation.edit.info

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.net.ConnectivityManager
import dev.olog.msc.api.last.fm.LastFmService
import dev.olog.msc.domain.interactor.detail.item.GetSongUseCase
import dev.olog.msc.utils.MediaId
import javax.inject.Inject

class EditSongFragmentViewModelFactory @Inject constructor(
        private val mediaId: MediaId,
        private val lastFm: LastFmService,
        private val getSongUseCase: GetSongUseCase,
        private val connectivityManager: ConnectivityManager

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EditSongFragmentViewModel(
                mediaId, lastFm, getSongUseCase,
                connectivityManager
        ) as T
    }
}