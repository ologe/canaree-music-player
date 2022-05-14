package dev.olog.feature.shortcuts

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.feature.shortcuts.api.AppShortcuts
import dev.olog.feature.shortcuts.api.FeatureShortcutsNavigator
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureShortcutsModule {

    @Binds
    @Singleton
    abstract fun provideNavigator(impl: FeatureShortcutsNavigatorImpl): FeatureShortcutsNavigator

    @Binds
    @Singleton
    abstract fun provideAppShortcuts(impl: AppShortcutsImp): AppShortcuts

}