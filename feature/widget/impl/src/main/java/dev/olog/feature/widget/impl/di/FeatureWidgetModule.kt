package dev.olog.feature.widget.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.feature.widget.api.FeatureWidgetNavigator
import dev.olog.feature.widget.impl.navigation.FeatureWidgetNavigatorImpl

@Module
@InstallIn(SingletonComponent::class)
interface FeatureWidgetModule {

    @Binds
    fun provideNavigator(impl: FeatureWidgetNavigatorImpl): FeatureWidgetNavigator

}