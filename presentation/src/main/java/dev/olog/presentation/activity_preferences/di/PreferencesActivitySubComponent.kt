package dev.olog.presentation.activity_preferences.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.activity_preferences.PreferencesActivity
import dev.olog.presentation.activity_preferences.neural_network.di.NeuralNetworkFragmentInjector
import dev.olog.presentation.dagger.AndroidBindingModule
import dev.olog.presentation.dagger.PerActivity


@Subcomponent(modules = arrayOf(
        AndroidBindingModule::class,
        NeuralNetworkFragmentInjector::class
))
@PerActivity
interface PreferencesActivitySubComponent : AndroidInjector<PreferencesActivity> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<PreferencesActivity>()

}