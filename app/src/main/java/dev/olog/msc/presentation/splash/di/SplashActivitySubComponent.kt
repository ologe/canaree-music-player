package dev.olog.msc.presentation.splash.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.msc.dagger.PerActivity
import dev.olog.msc.presentation.splash.SplashActivity


@Subcomponent(modules = arrayOf(
        SplashActivityModule::class,
        SplashActivityFragmentsModule::class
))
@PerActivity
interface SplashActivitySubComponent : AndroidInjector<SplashActivity> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<SplashActivity>() {

        abstract fun module(module: SplashActivityModule): Builder

        override fun seedInstance(instance: SplashActivity) {
            module(SplashActivityModule(instance))
        }
    }

}