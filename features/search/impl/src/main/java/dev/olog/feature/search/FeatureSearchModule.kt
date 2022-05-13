package dev.olog.feature.search

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
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