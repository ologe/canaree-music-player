package dev.olog.feature.main

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent
import dev.olog.feature.main.api.FeatureMainNavigator
import dev.olog.feature.main.api.FeatureMainPopupNavigator
import dev.olog.feature.main.api.MainPreferences
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureMainModule {

    @Binds
    @Singleton
    abstract fun provideNavigator(impl: FeatureMainNavigatorImpl): FeatureMainNavigator

    @Binds
    @Singleton
    abstract fun providePreferences(impl: MainPreferencesImpl): MainPreferences

}

@Module
@InstallIn(ActivityComponent::class)
abstract class FeatureActivityModule {

    @Binds
    @ActivityScoped
    abstract fun provideNavigator(impl: FeatureMainPopupNavigatorImpl): FeatureMainPopupNavigator

}