package dev.olog.msc.presentation.edit

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import javax.inject.Inject

class EditItemViewModelFactory @Inject constructor(
        private val presenter: EditItemPresenter

) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EditItemViewModel(presenter) as T
    }
}