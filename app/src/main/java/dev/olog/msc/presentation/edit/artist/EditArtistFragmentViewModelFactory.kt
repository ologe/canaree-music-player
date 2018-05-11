package dev.olog.msc.presentation.edit.artist

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import javax.inject.Inject

class EditArtistFragmentViewModelFactory @Inject constructor(
        private val presenter: EditArtistFragmentPresenter

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EditArtistFragmentViewModel(presenter) as T
    }
}