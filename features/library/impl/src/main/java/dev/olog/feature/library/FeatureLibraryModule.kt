package dev.olog.feature.library

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class FeatureLibraryModule {

    @Binds
    @Singleton
    abstract fun provideLibraryPrefs(impl: LibraryPrefsImpl): LibraryPrefs

}