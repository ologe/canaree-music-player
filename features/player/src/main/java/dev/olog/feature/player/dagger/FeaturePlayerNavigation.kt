package dev.olog.feature.player.dagger

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.multibindings.IntoMap
import dev.olog.feature.player.mini.PlayerMiniFragment
import dev.olog.feature.player.player.PlayerFragment
import dev.olog.navigation.dagger.FragmentScreenKey
import dev.olog.navigation.destination.FragmentScreen

@Module
@InstallIn(ApplicationComponent::class)
object FeaturePlayerDagger {

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.PLAYER)
    fun providePlayerFragment(): Fragment = PlayerFragment()

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.PLAYER_MINI)
    fun providePlayerMiniFragment(): Fragment = PlayerMiniFragment()

}