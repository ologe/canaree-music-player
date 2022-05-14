package dev.olog.feature.edit

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.feature.edit.api.FeatureEditNavigator
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureEditModule {

    @Binds
    @Singleton
    abstract fun provideNavigator(impl: FeatureEditNavigatorImpl): FeatureEditNavigator

}