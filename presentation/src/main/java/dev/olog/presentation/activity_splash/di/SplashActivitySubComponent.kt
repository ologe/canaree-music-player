package dev.olog.presentation.activity_splash.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.activity_splash.SplashActivity
import dev.olog.presentation.dagger.AndroidBindingModule
import dev.olog.presentation.dagger.PerActivity


@Subcomponent(modules = arrayOf(
        SplashActivityModule::class,
        AndroidBindingModule::class
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