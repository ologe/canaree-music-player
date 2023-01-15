package dev.olog.data.mediastore

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import dev.olog.core.AppInitializer
import dev.olog.data.blacklist.BlacklistDao
import dev.olog.data.db.dao.AppDatabase
import dev.olog.data.mediastore.audio.MediaStoreAudioDao

@Module
@InstallIn(SingletonComponent::class)
interface MediaStoreModule {

    @Binds
    @IntoSet
    fun provideMediaStoreIndexer(impl: MediaStoreIndexer): AppInitializer

    companion object {

        @Provides
        internal fun provideMediaStoreAudioDao(db: AppDatabase): MediaStoreAudioDao {
            return db.mediaStoreAudioDao()
        }

        @Provides
        internal fun provideBlacklistDao(db: AppDatabase): BlacklistDao {
            return db.blacklistDao()
        }

    }

}