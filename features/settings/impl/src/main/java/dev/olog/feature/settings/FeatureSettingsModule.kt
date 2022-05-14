package dev.olog.feature.settings

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.feature.settings.api.FeatureSettingsNavigator
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureSettingsModule {

    @Binds
    @Singleton
    abstract fun provideNavigator(impl: FeatureSettingsNavigatorImpl): FeatureSettingsNavigator

}