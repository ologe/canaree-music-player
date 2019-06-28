package dev.olog.presentation.prefs.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.dagger.PerActivity
import dev.olog.presentation.prefs.PreferencesActivity


@Subcomponent(modules = arrayOf(
        PreferencesActivityModule::class,
        PreferenceActivityFragmentsModule::class
))
@PerActivity
interface PreferencesActivitySubComponent : AndroidInjector<PreferencesActivity> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<PreferencesActivity>() {
        abstract fun module(module: PreferencesActivityModule): Builder

        override fun seedInstance(instance: PreferencesActivity) {
            module(PreferencesActivityModule(instance))
        }
    }

}