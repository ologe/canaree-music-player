package dev.olog.presentation.activity_neural_network.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.olog.presentation.activity_neural_network.NeuralNetworkFragment
import dev.olog.presentation.activity_neural_network.image_chooser.NeuralNetworkImageChooser
import dev.olog.presentation.activity_neural_network.style_chooser.NeuralNetworkStyleChooser

@Module
abstract class NeuralNetworkActivityModuleFragments {

    @ContributesAndroidInjector
    abstract fun provideNeuralNetworkStyleChooser(): NeuralNetworkStyleChooser

    @ContributesAndroidInjector
    abstract fun provideNeuralNetworkImageChooser(): NeuralNetworkImageChooser

    @ContributesAndroidInjector
    abstract fun provideNeuralNetworkFragmentChooserPage(): NeuralNetworkFragment

}