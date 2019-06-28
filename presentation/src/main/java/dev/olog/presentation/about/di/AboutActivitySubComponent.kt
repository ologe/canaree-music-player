package dev.olog.presentation.about.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.dagger.PerActivity
import dev.olog.presentation.about.AboutActivity

@Subcomponent(modules = arrayOf(
        AboutActivityModule::class
))
@PerActivity
interface AboutActivitySubComponent : AndroidInjector<AboutActivity> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<AboutActivity>() {

        abstract fun module(module: AboutActivityModule): Builder

        override fun seedInstance(instance: AboutActivity) {
            module(AboutActivityModule(instance))
        }
    }

}