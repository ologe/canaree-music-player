package dev.olog.msc.dagger

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.olog.msc.presentation.app.widget.WidgetClassic
import dev.olog.msc.presentation.app.widget.WidgetColored
import dev.olog.msc.presentation.dialog.sleep.timer.SleepTimerDialog
import dev.olog.msc.presentation.equalizer.EqualizerFragment
import dev.olog.msc.presentation.neural.network.service.NeuralNetworkService
import dev.olog.msc.presentation.preferences.blacklist.BlacklistFragment
import dev.olog.msc.presentation.preferences.categories.LibraryCategoriesFragment
import dev.olog.msc.presentation.splash.SplashFragment
import dev.olog.msc.presentation.splash.SplashFragmentTutorial

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

    @ContributesAndroidInjector
    abstract fun provideSleetTimerDialog() : SleepTimerDialog

    @ContributesAndroidInjector
    abstract fun provideEqualizerFragment(): EqualizerFragment

}