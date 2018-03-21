package dev.olog.msc.presentation.edit.album

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import javax.inject.Inject

class EditAlbumFragmentViewModelFactory @Inject constructor(
        private val application: Application,
        private val presenter: EditAlbumFragmentPresenter

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EditAlbumFragmentViewModel(
                application,
                presenter
        ) as T
    }
}