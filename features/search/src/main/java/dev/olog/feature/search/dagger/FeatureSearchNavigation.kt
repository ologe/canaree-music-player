package dev.olog.feature.search.dagger

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.multibindings.IntoMap
import dev.olog.feature.search.SearchFragment
import dev.olog.navigation.dagger.FragmentScreenKey
import dev.olog.navigation.destination.FragmentScreen

@Module
@InstallIn(ApplicationComponent::class)
object FeatureSearchNavigation {

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.SEARCH)
    fun provideLibraryTracksFragment(): Fragment = SearchFragment()

}