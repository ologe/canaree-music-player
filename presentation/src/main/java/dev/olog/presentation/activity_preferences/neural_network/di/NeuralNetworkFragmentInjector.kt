package dev.olog.presentation.activity_preferences.neural_network.di

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.presentation.activity_preferences.neural_network.NeuralNetworkFragment

@Module(subcomponents = arrayOf(NeuralNetworkFragmentSubComponent::class))
abstract class NeuralNetworkFragmentInjector {

    @Binds
    @IntoMap
    @FragmentKey(NeuralNetworkFragment::class)
    internal abstract fun injectorFactory(builder: NeuralNetworkFragmentSubComponent.Builder)
            : AndroidInjector.Factory<out Fragment>

}
