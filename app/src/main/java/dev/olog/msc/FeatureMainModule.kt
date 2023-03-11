package dev.olog.msc

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.feature.main.api.FeatureMainNavigator

@Module
@InstallIn(SingletonComponent::class)
// TODO move to :feature:main:impl
interface FeatureMainModule {

    @Binds
    fun provideNavigator(impl: FeatureMainNavigatorImpl): FeatureMainNavigator

}