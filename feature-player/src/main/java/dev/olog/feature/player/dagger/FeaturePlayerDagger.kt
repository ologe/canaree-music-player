package dev.olog.feature.player.dagger

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.core.dagger.FeatureScope
import dev.olog.feature.player.PlayerFragment
import dev.olog.feature.player.volume.PlayerVolumeFragment
import dev.olog.navigation.dagger.FragmentScreenKey
import dev.olog.navigation.screens.FragmentScreen

class FeaturePlayerDagger {

    @Module
    abstract class AppModule {

        @ContributesAndroidInjector(modules = [PlayerFragmentModule::class])
        @FeatureScope
        internal abstract fun provideFragment(): PlayerFragment

        @ContributesAndroidInjector
        @FeatureScope
        internal abstract fun provideVolumeFragment(): PlayerVolumeFragment

        companion object {

            @Provides
            @IntoMap
            @FragmentScreenKey(FragmentScreen.PLAYER)
            fun providePlayerFragment(): Fragment {
                return PlayerFragment()
            }

        }

    }

}