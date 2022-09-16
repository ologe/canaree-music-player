package dev.olog.data.playing

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.core.gateway.PlayingGateway
import dev.olog.data.db.AppDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PlayingModule {

    @Binds
    @Singleton
    abstract fun provideRepository(impl: PlayingRepository): PlayingGateway

    companion object {
        @Provides
        fun provideDao(db: AppDatabase) = db.playingDao()
    }

}