package dev.olog.msc.presentation.edit.album

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import javax.inject.Inject

class EditAlbumFragmentViewModelFactory @Inject constructor(
        private val presenter: EditAlbumFragmentPresenter

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EditAlbumFragmentViewModel(presenter) as T
    }
}