package dev.olog.feature.edit

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureInfoModule {

    @Binds
    @Singleton
    abstract fun provideNavigator(impl: FeatureInfoNavigatorImpl): FeatureInfoNavigator

}