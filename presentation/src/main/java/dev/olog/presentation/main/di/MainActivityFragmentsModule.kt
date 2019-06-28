package dev.olog.presentation.main.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.olog.presentation.equalizer.EqualizerFragment
import dev.olog.presentation.library.LibraryFragment
import dev.olog.presentation.offlinelyrics.OfflineLyricsFragment
import dev.olog.presentation.playermini.MiniPlayerFragment
import dev.olog.presentation.sleeptimer.SleepTimerPickerDialog

@Module
abstract class MainActivityFragmentsModule {

    @ContributesAndroidInjector
    abstract fun provideMiniPlayer(): MiniPlayerFragment

    @ContributesAndroidInjector
    abstract fun provideEqualizerFragment(): EqualizerFragment

    @ContributesAndroidInjector
    abstract fun provideSleepTimerDialog(): SleepTimerPickerDialog

    @ContributesAndroidInjector
    abstract fun provideOfflineLyricsFragment(): OfflineLyricsFragment

    @ContributesAndroidInjector
    abstract fun provideCategoriesFragment(): LibraryFragment
}