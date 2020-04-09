package dev.olog.presentation.main.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.olog.presentation.offlinelyrics.OfflineLyricsFragment
import dev.olog.presentation.playermini.MiniPlayerFragment
import dev.olog.presentation.sleeptimer.SleepTimerPickerDialog
import dev.olog.presentation.widgets.bottomnavigator.CanareeBottomNavigationView

@Module
abstract class MainActivityFragmentsModule {

    @ContributesAndroidInjector
    internal abstract fun provideMiniPlayer(): MiniPlayerFragment

    @ContributesAndroidInjector
    internal abstract fun provideSleepTimerDialog(): SleepTimerPickerDialog

    @ContributesAndroidInjector
    internal abstract fun provideOfflineLyricsFragment(): OfflineLyricsFragment



    // TODO probably won't work, use a real subcomponent
    @ContributesAndroidInjector
    internal abstract fun provideBottomSheetFragment(): CanareeBottomNavigationView
}