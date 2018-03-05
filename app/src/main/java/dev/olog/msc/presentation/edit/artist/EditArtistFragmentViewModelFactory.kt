package dev.olog.msc.presentation.edit.artist

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

class EditArtistFragmentViewModelFactory(
        private val application: Application

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EditArtistFragmentViewModel(
                application
        ) as T
    }
}