package dev.olog.feature.about

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureAboutModule {

    @Binds
    @Singleton
    abstract fun provideNavigator(impl: FeatureAboutNavigatorImpl): FeatureAboutNavigator

}