package dev.olog.feature.library

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import dev.olog.core.Resettable
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeatureLibraryModule {

    @Binds
    @Singleton
    abstract fun providePrefs(impl: LibraryPreferencesImpl): LibraryPreferences

    @Binds
    @IntoSet
    abstract fun provideResettable(impl: LibraryPreferences): Resettable

}