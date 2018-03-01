package dev.olog.msc.presentation.preferences.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.dagger.scope.PerActivity
import dev.olog.msc.presentation.preferences.PreferencesActivity


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