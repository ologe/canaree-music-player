package dev.olog.feature.offline.lyrics.dagger

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.multibindings.IntoMap
import dev.olog.feature.offline.lyrics.OfflineLyricsFragment
import dev.olog.navigation.dagger.FragmentScreenKey
import dev.olog.navigation.destination.FragmentScreen

@Module
@InstallIn(ApplicationComponent::class)
object FeatureOfflineLyricsNavigation {

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.OFFLINE_LYRICS)
    fun provideFragment(): Fragment = OfflineLyricsFragment()

}