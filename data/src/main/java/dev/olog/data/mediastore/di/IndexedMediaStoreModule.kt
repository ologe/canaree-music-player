package dev.olog.data.mediastore.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import dev.olog.core.AppInitializer
import dev.olog.data.db.AppDatabase
import dev.olog.data.mediastore.IndexedMediaStoreInitializer

@Module
@InstallIn(SingletonComponent::class)
internal abstract class IndexedMediaStoreModule {

    @Binds
    @IntoSet
    abstract fun provideInitializer(impl: IndexedMediaStoreInitializer): AppInitializer

    companion object {
        @Provides
        fun provideMediaStoreAudioDao(db: AppDatabase) = db.mediaStoreAudioDao()
        @Provides
        fun provideMediaStoreGenreDao(db: AppDatabase) = db.mediaStoreGenreDao()
        @Provides
        fun provideMediaStorePlaylistDao(db: AppDatabase) = db.mediaStorePlaylistDao()
    }

}