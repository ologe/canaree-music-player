package dev.olog.presentation.dagger

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.olog.presentation.activity_splash.SplashFragment
import dev.olog.presentation.activity_splash.SplashFragmentTutorial

@Module
abstract class AndroidBindingModule {

    @ContributesAndroidInjector
    abstract fun provideSplashFragmentTutorial(): SplashFragmentTutorial

    @ContributesAndroidInjector
    abstract fun provideSplashFragment(): SplashFragment

}