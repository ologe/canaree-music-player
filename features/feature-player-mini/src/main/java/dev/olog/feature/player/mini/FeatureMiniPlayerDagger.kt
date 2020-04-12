package dev.olog.feature.player.mini

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.core.dagger.FeatureScope
import dev.olog.navigation.dagger.FragmentScreenKey
import dev.olog.navigation.screens.FragmentScreen

class FeatureMiniPlayerDagger {

    @Module
    abstract class AppModule {

        @ContributesAndroidInjector
        @FeatureScope
        internal abstract fun provideMiniPlayer(): MiniPlayerFragment

        companion object {
            @Provides
            @IntoMap
            @FragmentScreenKey(FragmentScreen.PLAYER_MINI)
            fun providePlayerFragment(): Fragment {
                return MiniPlayerFragment()
            }
        }

    }

}