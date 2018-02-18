package dev.olog.msc.presentation.edit.info

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.msc.api.last.fm.LastFm
import dev.olog.msc.domain.interactor.detail.item.GetSongUseCase
import dev.olog.msc.utils.MediaId
import javax.inject.Inject

class EditSongFragmentViewModelFactory @Inject constructor(
        private val mediaId: MediaId,
        private val lastFm: LastFm,
        private val getSongUseCase: GetSongUseCase

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EditSongFragmentViewModel(
                mediaId, lastFm, getSongUseCase
        ) as T
    }
}