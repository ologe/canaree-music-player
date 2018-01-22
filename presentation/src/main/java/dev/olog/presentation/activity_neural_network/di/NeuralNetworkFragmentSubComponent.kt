package dev.olog.presentation.activity_neural_network.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.activity_neural_network.NeuralNetworkActivity
import dev.olog.presentation.dagger.PerActivity

@Subcomponent(modules = arrayOf(
        NeuralNetworkActivityModule::class,
        NeuralNetworkActivityModuleFragments::class
))
@PerActivity
interface NeuralNetworkFragmentSubComponent : AndroidInjector<NeuralNetworkActivity> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<NeuralNetworkActivity>() {

        abstract fun module(module: NeuralNetworkActivityModule): Builder

        override fun seedInstance(instance: NeuralNetworkActivity) {
            module(NeuralNetworkActivityModule(instance))
        }

    }

}