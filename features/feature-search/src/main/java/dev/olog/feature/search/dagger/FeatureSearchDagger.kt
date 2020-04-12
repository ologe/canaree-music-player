package dev.olog.feature.search.dagger

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.core.dagger.FeatureScope
import dev.olog.feature.search.SearchFragment
import dev.olog.navigation.dagger.FragmentScreenKey
import dev.olog.navigation.screens.FragmentScreen

class FeatureSearchDagger {

    @Module
    abstract class AppModule {

        @ContributesAndroidInjector(modules = [SearchModule::class])
        @FeatureScope
        abstract fun provideSearchFragment(): SearchFragment

        companion object {

            @Provides
            @IntoMap
            @FragmentScreenKey(FragmentScreen.SEARCH)
            internal fun provideFragment(): Fragment {
                return SearchFragment.newInstance()
            }

        }

    }

}