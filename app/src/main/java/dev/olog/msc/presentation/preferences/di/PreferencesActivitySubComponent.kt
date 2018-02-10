package dev.olog.msc.presentation.preferences.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.dagger.AndroidBindingModule
import dev.olog.msc.dagger.PerActivity
import dev.olog.msc.presentation.preferences.PreferencesActivity


@Subcomponent(modules = arrayOf(
        AndroidBindingModule::class
))
@PerActivity
interface PreferencesActivitySubComponent : AndroidInjector<PreferencesActivity> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<PreferencesActivity>()

}