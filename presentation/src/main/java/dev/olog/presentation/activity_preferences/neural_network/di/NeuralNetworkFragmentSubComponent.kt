package dev.olog.presentation.activity_preferences.neural_network.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.activity_preferences.neural_network.NeuralNetworkFragment
import dev.olog.presentation.dagger.PerFragment

@Subcomponent()
@PerFragment
interface NeuralNetworkFragmentSubComponent : AndroidInjector<NeuralNetworkFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<NeuralNetworkFragment>()

}