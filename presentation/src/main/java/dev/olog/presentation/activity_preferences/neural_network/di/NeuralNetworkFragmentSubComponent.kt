package dev.olog.presentation.activity_preferences.neural_network.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.activity_preferences.neural_network.NeuralNetworkFragment
import dev.olog.presentation.dagger.PerFragment

@Subcomponent(modules = arrayOf(
        NeuralNetworkFragmentModule::class
))
@PerFragment
interface NeuralNetworkFragmentSubComponent : AndroidInjector<NeuralNetworkFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<NeuralNetworkFragment>() {

        abstract fun module(module: NeuralNetworkFragmentModule): Builder

        override fun seedInstance(instance: NeuralNetworkFragment) {
            module(NeuralNetworkFragmentModule(instance))
        }

    }

}