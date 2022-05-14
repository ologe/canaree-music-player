package dev.olog.feature.queue

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import dev.olog.feature.queue.api.FeatureQueueNavigator
import dev.olog.platform.BottomNavigationFragmentTag
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureQueueModule {

    @Binds
    @Singleton
    abstract fun provideNavigator(impl: FeatureQueueNavigatorImpl): FeatureQueueNavigator

    companion object {

        @Provides
        @IntoSet
        fun provideQueueTag(): BottomNavigationFragmentTag {
            return BottomNavigationFragmentTag { PlayingQueueFragment.TAG }
        }

    }

}