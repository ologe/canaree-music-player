package dev.olog.msc.app

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.olog.msc.presentation.splash.SplashActivity

@Module
abstract class ActivityBindingsModule {

    @ContributesAndroidInjector
    abstract fun provideSplashActivity(): SplashActivity

}