package dev.olog.feature.library.dagger

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.multibindings.IntoMap
import dev.olog.feature.library.home.HomeFragment
import dev.olog.feature.library.library.LibraryChooserFragment
import dev.olog.feature.library.library.LibraryFragment
import dev.olog.feature.library.span.LibrarySpanFragment
import dev.olog.navigation.dagger.FragmentScreenKey
import dev.olog.navigation.screens.FragmentScreen

@Module
@InstallIn(ApplicationComponent::class)
class LibraryNavigationModule {

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.HOME)
    internal fun provideHomeFragment(): Fragment {
        return HomeFragment()
    }

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.LIBRARY)
    internal fun provideLibraryFragment(): Fragment {
        return LibraryFragment()
    }

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.LIBRARY_CHOOSER)
    internal fun provideLibraryChooserFragment(): Fragment {
        return LibraryChooserFragment()
    }

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.LIBRARY_SPAN)
    internal fun provideLibrarySpanFragment(): Fragment {
        return LibrarySpanFragment()
    }

}