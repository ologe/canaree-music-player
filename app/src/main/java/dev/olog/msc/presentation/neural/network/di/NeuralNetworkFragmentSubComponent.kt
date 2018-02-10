package dev.olog.msc.presentation.neural.network.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.dagger.PerActivity
import dev.olog.msc.presentation.neural.network.NeuralNetworkActivity

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