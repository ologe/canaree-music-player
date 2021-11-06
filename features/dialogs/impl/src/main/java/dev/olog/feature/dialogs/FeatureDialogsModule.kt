package dev.olog.feature.dialogs

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureDialogsModule {

    @Binds
    @Singleton
    abstract fun provideNavigator(impl: FeatureDialogsNavigatorImpl): FeatureDialogsNavigator

}