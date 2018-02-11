package dev.olog.msc.presentation.main.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.olog.msc.presentation.dialog.sleep.timer.SleepTimerDialog
import dev.olog.msc.presentation.equalizer.EqualizerFragment
import dev.olog.msc.presentation.mini.player.MiniPlayerFragment

@Module
abstract class MainActivityFragmentsModule {

    @ContributesAndroidInjector
    abstract fun provideMiniPlayer() : MiniPlayerFragment

    @ContributesAndroidInjector
    abstract fun provideEqualizerFragment(): EqualizerFragment

    @ContributesAndroidInjector
    abstract fun provideSleetTimerDialog() : SleepTimerDialog

}