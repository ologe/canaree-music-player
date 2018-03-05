package dev.olog.msc.presentation.edit.artist

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import javax.inject.Inject

class EditArtistFragmentViewModelFactory @Inject constructor(
        private val application: Application,
        private val presenter: EditArtistFragmentPresenter

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EditArtistFragmentViewModel(
                application,
                presenter
        ) as T
    }
}