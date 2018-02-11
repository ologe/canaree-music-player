package dev.olog.msc.presentation.splash.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.olog.msc.presentation.splash.presentation.SplashPresentationFragment
import dev.olog.msc.presentation.splash.tutorial.SplashTutorialFragment

@Module
abstract class SplashActivityFragmentsModule {

    @ContributesAndroidInjector
    abstract fun provideSplashFragmentTutorial(): SplashTutorialFragment

    @ContributesAndroidInjector
    abstract fun provideSplashFragment(): SplashPresentationFragment

}