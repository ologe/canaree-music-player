package dev.olog.presentation.dagger

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.olog.presentation.activity_neural_network.service.NeuralNetworkService
import dev.olog.presentation.activity_preferences.blacklist.BlacklistFragment
import dev.olog.presentation.activity_preferences.categories.LibraryCategoriesFragment
import dev.olog.presentation.activity_splash.SplashFragment
import dev.olog.presentation.activity_splash.SplashFragmentTutorial
import dev.olog.presentation.widget_home_screen.WidgetClassic
import dev.olog.presentation.widget_home_screen.WidgetColored

@Module
abstract class AndroidBindingModule {

    @ContributesAndroidInjector
    abstract fun provideSplashFragmentTutorial(): SplashFragmentTutorial

    @ContributesAndroidInjector
    abstract fun provideSplashFragment(): SplashFragment

    @ContributesAndroidInjector
    abstract fun provideLibraryCategoriesFragment() : LibraryCategoriesFragment

    @ContributesAndroidInjector
    abstract fun provideBlacklistFragment() : BlacklistFragment

    @ContributesAndroidInjector
    abstract fun provideWidgetColored() : WidgetColored

    @ContributesAndroidInjector
    abstract fun provideWidgetClassic() : WidgetClassic

    @ContributesAndroidInjector
    abstract fun provideNeuralService() : NeuralNetworkService

}