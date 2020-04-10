package dev.olog.presentation.main.di

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.navigation.dagger.FragmentScreenKey
import dev.olog.navigation.screens.FragmentScreen
import dev.olog.presentation.offlinelyrics.OfflineLyricsFragment
import dev.olog.presentation.playermini.MiniPlayerFragment
import dev.olog.presentation.sleeptimer.SleepTimerPickerDialog
import dev.olog.presentation.widgets.bottomnavigator.CanareeBottomNavigationView

@Module
abstract class MainActivityFragmentsModule {

    @ContributesAndroidInjector
    internal abstract fun provideMiniPlayer(): MiniPlayerFragment

    companion object {
        @Provides
        @IntoMap
        @FragmentScreenKey(FragmentScreen.MINI_PLAYER)
        fun providePlayerFragment(): Fragment {
            return MiniPlayerFragment()
        }
    }

    @ContributesAndroidInjector
    internal abstract fun provideSleepTimerDialog(): SleepTimerPickerDialog

    @ContributesAndroidInjector
    internal abstract fun provideOfflineLyricsFragment(): OfflineLyricsFragment



    // TODO probably won't work, use a real subcomponent
    @ContributesAndroidInjector
    internal abstract fun provideBottomSheetFragment(): CanareeBottomNavigationView
}