package dev.olog.presentation.main.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.olog.presentation.library.LibraryFragment
import dev.olog.presentation.offlinelyrics.OfflineLyricsFragment
import dev.olog.presentation.playermini.MiniPlayerFragment
import dev.olog.presentation.sleeptimer.SleepTimerPickerDialog

@Module
abstract class MainActivityFragmentsModule {

    @ContributesAndroidInjector
    internal abstract fun provideMiniPlayer(): MiniPlayerFragment

    @ContributesAndroidInjector
    internal abstract fun provideSleepTimerDialog(): SleepTimerPickerDialog

    @ContributesAndroidInjector
    internal abstract fun provideOfflineLyricsFragment(): OfflineLyricsFragment

    @ContributesAndroidInjector
    internal abstract fun provideCategoriesFragment(): LibraryFragment
}