package dev.olog.feature.library.dagger

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.multibindings.IntoMap
import dev.olog.feature.library.library.LibraryFragment
import dev.olog.feature.library.library.prefs.LibraryPrefsFragment
import dev.olog.navigation.dagger.FragmentScreenKey
import dev.olog.navigation.destination.FragmentScreen

@Module
@InstallIn(ApplicationComponent::class)
object FeatureLibraryNavigation {

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.LIBRARY_TRACKS)
    fun provideLibraryTracksFragment(): Fragment = LibraryFragment.newInstance(false)

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.LIBRARY_PODCASTS)
    fun provideLibraryPodcastsFragment(): Fragment = LibraryFragment.newInstance(false)

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.LIBRARY_PREFS)
    fun provideLibraryPrefsFragment(): Fragment = LibraryPrefsFragment()

}