package dev.olog.appshortcuts

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dev.olog.core.AppShortcuts
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
internal abstract class AppShortcutsModule {

    @Binds
    @Singleton
    abstract fun provideImpl(impl: AppShortcutsImpl): AppShortcuts

}