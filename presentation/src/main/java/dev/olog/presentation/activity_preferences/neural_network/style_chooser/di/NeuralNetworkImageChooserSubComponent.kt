package dev.olog.presentation.activity_preferences.neural_network.style_chooser.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.activity_preferences.neural_network.style_chooser.NeuralNetworkImageChooser
import dev.olog.presentation.dagger.PerFragment

@Subcomponent(modules = arrayOf(
        NeuralNetworkImageChooserModule::class
))
@PerFragment
interface NeuralNetworkImageChooserSubComponent : AndroidInjector<NeuralNetworkImageChooser> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<NeuralNetworkImageChooser>() {

        abstract fun module(module: NeuralNetworkImageChooserModule): Builder

        override fun seedInstance(instance: NeuralNetworkImageChooser) {
            module(NeuralNetworkImageChooserModule(instance))
        }
    }

}