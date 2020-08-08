package dev.olog.feature.search.dagger

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.multibindings.IntoMap
import dev.olog.feature.search.SearchFragment
import dev.olog.navigation.dagger.FragmentScreenKey
import dev.olog.navigation.screens.FragmentScreen

@Module
@InstallIn(ApplicationComponent::class)
object SearchNavigationModule {

    @Provides
    @IntoMap
    @FragmentScreenKey(FragmentScreen.SEARCH)
    internal fun provideFragment(): Fragment {
        return SearchFragment.newInstance()
    }

}