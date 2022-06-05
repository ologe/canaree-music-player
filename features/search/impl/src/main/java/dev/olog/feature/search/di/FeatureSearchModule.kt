package dev.olog.feature.search.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import dev.olog.feature.search.SearchFragment
import dev.olog.feature.search.api.FeatureSearchNavigator
import dev.olog.feature.search.navigation.FeatureSearchNavigatorImpl
import dev.olog.platform.BottomNavigationFragmentTag
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureSearchModule {

    @Binds
    @Singleton
    abstract fun provideNavigator(impl: FeatureSearchNavigatorImpl): FeatureSearchNavigator

    companion object {

        @Provides
        @IntoSet
        fun provideSearchTag(): BottomNavigationFragmentTag {
            return BottomNavigationFragmentTag { SearchFragment.TAG }
        }

    }

}