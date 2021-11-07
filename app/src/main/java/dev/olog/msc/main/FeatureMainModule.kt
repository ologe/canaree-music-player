package dev.olog.msc.main

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.feature.main.FeatureMainNavigator
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureMainModule {

    @Binds
    @Singleton
    abstract fun provideNavigator(impl: FeatureMainNavigatorImpl): FeatureMainNavigator

}