package dev.olog.feature.library.dagger

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.multibindings.IntoMap
import dev.olog.feature.library.library.LibraryFragment
import dev.olog.navigation.dagger.FragmentScreenKey
import dev.olog.navigation.screens.FragmentScreen

@Module
@InstallIn(ApplicationComponent::class)
class LibraryNavigationModule {

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.HOME)
    internal fun provideLibraryTracksFragment(): Fragment {
        return LibraryFragment.newInstance(false)
    }

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.LIBRARY)
    internal fun provideLibraryPodcastFragment(): Fragment {
        return LibraryFragment.newInstance(true)
    }

}