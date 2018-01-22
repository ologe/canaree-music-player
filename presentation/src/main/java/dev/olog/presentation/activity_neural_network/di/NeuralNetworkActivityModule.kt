package dev.olog.presentation.activity_neural_network.di

import android.arch.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import dev.olog.presentation.activity_neural_network.NeuralNetworkActivity
import dev.olog.presentation.activity_neural_network.NeuralNetworkActivityViewModel
import dev.olog.presentation.activity_neural_network.NeuralNetworkActivityViewModelFactory

@Module
class NeuralNetworkActivityModule(
        private val activity: NeuralNetworkActivity
) {

    @Provides
    fun provideViewModel(factory: NeuralNetworkActivityViewModelFactory) : NeuralNetworkActivityViewModel {
        return ViewModelProviders.of(activity, factory).get(NeuralNetworkActivityViewModel::class.java)
    }

}