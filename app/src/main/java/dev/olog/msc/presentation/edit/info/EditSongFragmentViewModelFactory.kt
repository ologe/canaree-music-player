package dev.olog.msc.presentation.edit.info

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.msc.api.last.fm.LastFmClient
import dev.olog.msc.domain.interactor.detail.item.GetSongUseCase
import dev.olog.msc.utils.MediaId
import javax.inject.Inject

class EditSongFragmentViewModelFactory @Inject constructor(
        private val application: Application,
        private val mediaId: MediaId,
        private val lastFm: LastFmClient,
        private val getSongUseCase: GetSongUseCase

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EditSongFragmentViewModel(
                application, mediaId, lastFm, getSongUseCase
        ) as T
    }
}