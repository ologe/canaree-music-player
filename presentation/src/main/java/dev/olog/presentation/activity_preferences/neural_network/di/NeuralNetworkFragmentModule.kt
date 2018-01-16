package dev.olog.presentation.activity_preferences.neural_network.di

import android.arch.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import dev.olog.presentation.activity_preferences.neural_network.NeuralNetworkFragment
import dev.olog.presentation.activity_preferences.neural_network.NeuralNetworkFragmentViewModel
import dev.olog.presentation.activity_preferences.neural_network.NeuralNetworkFragmentViewModelFactory

@Module
class NeuralNetworkFragmentModule(
private val fragment: NeuralNetworkFragment
) {

    @Provides
    fun provideViewModel(factory: NeuralNetworkFragmentViewModelFactory) : NeuralNetworkFragmentViewModel {
        return ViewModelProviders.of(fragment.activity!!, factory).get(NeuralNetworkFragmentViewModel::class.java)
    }

}