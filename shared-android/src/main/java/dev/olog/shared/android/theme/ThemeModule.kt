package dev.olog.shared.android.theme

import dagger.Binds
import dagger.Module
import dagger.Reusable

@Module
abstract class ThemeModule {

    @Binds
    @Reusable
    internal abstract fun provideThemeManager(impl: ThemeManagerImpl): ThemeManager

}