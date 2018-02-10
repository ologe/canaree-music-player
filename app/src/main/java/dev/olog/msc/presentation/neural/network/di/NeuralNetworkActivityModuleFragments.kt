package dev.olog.msc.presentation.neural.network.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.olog.msc.presentation.neural.network.NeuralNetworkFragment
import dev.olog.msc.presentation.neural.network.image.chooser.NeuralNetworkImageChooser
import dev.olog.msc.presentation.neural.network.style.chooser.NeuralNetworkStyleChooser

@Module
abstract class NeuralNetworkActivityModuleFragments {

    @ContributesAndroidInjector
    abstract fun provideNeuralNetworkStyleChooser(): NeuralNetworkStyleChooser

    @ContributesAndroidInjector
    abstract fun provideNeuralNetworkImageChooser(): NeuralNetworkImageChooser

    @ContributesAndroidInjector
    abstract fun provideNeuralNetworkFragmentChooserPage(): NeuralNetworkFragment

}