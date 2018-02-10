package dev.olog.msc.presentation.neural.network.di

import android.app.Activity
import dagger.Binds
import dagger.Module
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.neural.network.NeuralNetworkActivity

@Module(subcomponents = arrayOf(NeuralNetworkFragmentSubComponent::class))
abstract class NeuralNetworkActivityInjector {

    @Binds
    @IntoMap
    @ActivityKey(NeuralNetworkActivity::class)
    internal abstract fun injectorFactory(builder: NeuralNetworkFragmentSubComponent.Builder)
            : AndroidInjector.Factory<out Activity>

}
