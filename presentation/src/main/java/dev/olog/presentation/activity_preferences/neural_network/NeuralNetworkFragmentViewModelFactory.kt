package dev.olog.presentation.activity_preferences.neural_network

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.ContentResolver
import dev.olog.domain.interactor.tab.GetAllAlbumsUseCase
import dev.olog.presentation.dagger.PerActivity
import javax.inject.Inject

@PerActivity
class NeuralNetworkFragmentViewModelFactory @Inject constructor(
        private val contentResolver: ContentResolver,
        private val getAllAlbumsUseCase: GetAllAlbumsUseCase

): ViewModelProvider.Factory {


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NeuralNetworkFragmentViewModel(
                contentResolver, getAllAlbumsUseCase
        ) as T
    }
}