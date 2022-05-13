package dev.olog.feature.library

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import dev.olog.core.Resettable
import dev.olog.platform.BottomNavigationFragmentTag
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureLibraryModule {

    @Binds
    @Singleton
    abstract fun provideNavigator(impl: FeatureLibraryNavigatorImpl): FeatureLibraryNavigator

    @Binds
    @Singleton
    abstract fun providePrefs(impl: LibraryPreferencesImpl): LibraryPreferences

    @Binds
    @IntoSet
    abstract fun provideResettable(impl: LibraryPreferences): Resettable

    companion object {

        @Provides
        @IntoSet
        fun provideLibraryTrackTag(): BottomNavigationFragmentTag {
            return BottomNavigationFragmentTag { LibraryFragment.TAG_TRACK }
        }

        @Provides
        @IntoSet
        fun provideLibraryPodcastTag(): BottomNavigationFragmentTag {
            return BottomNavigationFragmentTag { LibraryFragment.TAG_PODCAST }
        }

    }

}