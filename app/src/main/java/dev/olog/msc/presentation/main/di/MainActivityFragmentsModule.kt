package dev.olog.msc.presentation.main.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.olog.msc.presentation.dialog.sleep.timer.SleepTimerPickerDialog
import dev.olog.msc.presentation.equalizer.EqualizerFragment
import dev.olog.presentation.library.LibraryFragment
import dev.olog.presentation.playermini.MiniPlayerFragment
import dev.olog.msc.presentation.offline.lyrics.OfflineLyricsFragment

@Module
abstract class MainActivityFragmentsModule {

    @ContributesAndroidInjector
    abstract fun provideMiniPlayer() : MiniPlayerFragment

    @ContributesAndroidInjector
    abstract fun provideEqualizerFragment(): EqualizerFragment

    @ContributesAndroidInjector
    abstract fun provideSleepTimerDialog() : SleepTimerPickerDialog

    @ContributesAndroidInjector
    abstract fun provideOfflineLyricsFragment(): OfflineLyricsFragment

    @ContributesAndroidInjector
    abstract fun provideCategoriesFragment(): LibraryFragment
}