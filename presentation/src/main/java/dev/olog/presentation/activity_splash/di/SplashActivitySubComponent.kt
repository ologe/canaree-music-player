package dev.olog.presentation.activity_splash.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.activity_splash.SplashActivity
import dev.olog.presentation.dagger.PerActivity
import dev.olog.presentation.navigation.NavigatorModule


@Subcomponent(modules = arrayOf(
        SplashActivityModule::class,
        NavigatorModule::class
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