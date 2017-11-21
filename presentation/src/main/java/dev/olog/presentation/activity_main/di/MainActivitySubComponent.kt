package dev.olog.presentation.activity_main.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.activity_main.MainActivity
import dev.olog.presentation.dagger.PerActivity
import dev.olog.presentation.fragment_tab.di.TabFragmentInjector
import dev.olog.presentation.navigation.NavigatorModule

@Subcomponent(modules = arrayOf(
        MainActivityModule::class,
        NavigatorModule::class,

        // fragments
        TabFragmentInjector::class
))
@PerActivity
interface MainActivitySubComponent :AndroidInjector<MainActivity> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<MainActivity>() {

        abstract fun module(module: MainActivityModule): Builder

        override fun seedInstance(instance: MainActivity) {
            module(MainActivityModule(instance))
        }
    }

}