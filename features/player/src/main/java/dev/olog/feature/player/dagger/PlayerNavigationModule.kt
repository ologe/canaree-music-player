package dev.olog.feature.player.dagger

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.multibindings.IntoMap
import dev.olog.feature.player.PlayerFragment
import dev.olog.navigation.dagger.FragmentScreenKey
import dev.olog.navigation.screens.FragmentScreen

@Module
@InstallIn(ApplicationComponent::class)
object PlayerNavigationModule {

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.PLAYER)
    fun providePlayerFragment(): Fragment {
        return PlayerFragment()
    }

}