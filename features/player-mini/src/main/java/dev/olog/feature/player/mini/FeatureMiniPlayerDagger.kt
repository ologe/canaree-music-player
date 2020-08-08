package dev.olog.feature.player.mini

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.multibindings.IntoMap
import dev.olog.navigation.dagger.FragmentScreenKey
import dev.olog.navigation.screens.FragmentScreen

@Module
@InstallIn(ApplicationComponent::class)
class FeatureMiniPlayerDagger {

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.PLAYER_MINI)
    fun providePlayerFragment(): Fragment {
        return MiniPlayerFragment()
    }

}