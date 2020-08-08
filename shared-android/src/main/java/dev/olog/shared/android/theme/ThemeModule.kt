package dev.olog.shared.android.theme

import dagger.Binds
import dagger.Module
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@Module
@InstallIn(ApplicationComponent::class)
abstract class ThemeModule {

    @Binds
    @Reusable
    internal abstract fun provideThemeManager(impl: ThemeManagerImpl): ThemeManager

}