package dev.olog.msc.presentation.neural.network

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.ContentResolver
import dev.olog.msc.dagger.PerActivity
import dev.olog.msc.domain.interactor.GetAllAlbumsForUtilsUseCase
import javax.inject.Inject

@PerActivity
class NeuralNetworkActivityViewModelFactory @Inject constructor(
        private val contentResolver: ContentResolver,
        private val getAllAlbumsUseCase: GetAllAlbumsForUtilsUseCase

): ViewModelProvider.Factory {


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NeuralNetworkActivityViewModel(
                contentResolver, getAllAlbumsUseCase
        ) as T
    }
}