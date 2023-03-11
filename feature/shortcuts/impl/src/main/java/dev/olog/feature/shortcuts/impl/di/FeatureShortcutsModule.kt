package dev.olog.feature.shortcuts.impl.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import dev.olog.core.AppInitializer
import dev.olog.feature.shortcuts.api.FeatureShortcutsNavigator
import dev.olog.feature.shortcuts.impl.AppShortcuts
import dev.olog.feature.shortcuts.impl.navigation.FeatureShortcutsNavigatorImpl

@Module
@InstallIn(SingletonComponent::class)
interface FeatureShortcutsModule {

    @Binds
    fun provideNavigator(impl: FeatureShortcutsNavigatorImpl): FeatureShortcutsNavigator

    @Binds
    @IntoSet
    fun provideAppShorcutsInitializer(impl: AppShortcuts): AppInitializer

}