package dev.olog.msc.presentation.neural.network.di

import android.arch.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import dev.olog.msc.presentation.neural.network.NeuralNetworkActivity
import dev.olog.msc.presentation.neural.network.NeuralNetworkActivityViewModel
import dev.olog.msc.presentation.neural.network.NeuralNetworkActivityViewModelFactory

@Module
class NeuralNetworkActivityModule(
        private val activity: NeuralNetworkActivity
) {

    @Provides
    fun provideViewModel(factory: NeuralNetworkActivityViewModelFactory) : NeuralNetworkActivityViewModel {
        return ViewModelProviders.of(activity, factory).get(NeuralNetworkActivityViewModel::class.java)
    }

}