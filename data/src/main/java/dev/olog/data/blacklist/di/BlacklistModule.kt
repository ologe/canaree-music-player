package dev.olog.data.blacklist.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.core.BlacklistGateway
import dev.olog.data.blacklist.BlacklistRepository
import dev.olog.data.db.AppDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class BlacklistModule {

    @Binds
    @Singleton
    abstract fun provideBlacklistPreferences(impl: BlacklistRepository): BlacklistGateway

    companion object {

        @Provides
        fun provideBlacklistDao(db: AppDatabase) = db.blacklistDao()

    }

}