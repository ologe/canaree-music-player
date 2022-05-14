package dev.olog.feature.widget

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.feature.widget.api.FeatureWidgetNavigator
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureWidgetModule {

    @Binds
    @Singleton
    abstract fun provideNavigator(impl: FeatureWidgetNavigatorImpl): FeatureWidgetNavigator

}