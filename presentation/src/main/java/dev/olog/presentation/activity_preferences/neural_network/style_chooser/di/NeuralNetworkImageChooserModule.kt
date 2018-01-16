package dev.olog.presentation.activity_preferences.neural_network.style_chooser.di

import android.arch.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import dev.olog.presentation.activity_preferences.neural_network.NeuralNetworkFragmentViewModel
import dev.olog.presentation.activity_preferences.neural_network.NeuralNetworkFragmentViewModelFactory
import dev.olog.presentation.activity_preferences.neural_network.style_chooser.NeuralNetworkImageChooser


@Module
class NeuralNetworkImageChooserModule(
        private val fragment: NeuralNetworkImageChooser
) {

    @Provides
    fun provideViewModel(factory: NeuralNetworkFragmentViewModelFactory) : NeuralNetworkFragmentViewModel {
        return ViewModelProviders.of(fragment.activity!!, factory).get(NeuralNetworkFragmentViewModel::class.java)
    }

}