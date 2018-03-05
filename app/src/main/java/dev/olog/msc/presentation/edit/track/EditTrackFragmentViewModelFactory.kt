package dev.olog.msc.presentation.edit.track

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dev.olog.msc.presentation.NetworkConnectionPublisher
import javax.inject.Inject

class EditTrackFragmentViewModelFactory @Inject constructor(
        private val application: Application,
        private val connectionPublisher: NetworkConnectionPublisher,
        private val presenter: EditTrackFragmentPresenter

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EditTrackFragmentViewModel(
                application,
                connectionPublisher,
                presenter
        ) as T
    }
}