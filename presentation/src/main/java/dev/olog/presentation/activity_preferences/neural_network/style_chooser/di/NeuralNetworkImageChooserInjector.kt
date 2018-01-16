package dev.olog.presentation.activity_preferences.neural_network.style_chooser.di

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.presentation.activity_preferences.neural_network.style_chooser.NeuralNetworkImageChooser

@Module(subcomponents = arrayOf(NeuralNetworkImageChooserSubComponent::class))
abstract class NeuralNetworkImageChooserInjector {

    @Binds
    @IntoMap
    @FragmentKey(NeuralNetworkImageChooser::class)
    internal abstract fun injectorFactory(builder: NeuralNetworkImageChooserSubComponent.Builder)
            : AndroidInjector.Factory<out Fragment>

}
