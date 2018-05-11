package dev.olog.msc.presentation.edit.track

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import javax.inject.Inject

class EditTrackFragmentViewModelFactory @Inject constructor(
        private val presenter: EditTrackFragmentPresenter

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EditTrackFragmentViewModel(presenter) as T
    }
}