package dev.olog.data.podcast

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.core.gateway.podcast.PodcastGateway
import dev.olog.data.db.AppDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PodcastModule {

    @Binds
    @Singleton
    internal abstract fun providePodcastRepository(repository: PodcastRepository): PodcastGateway

    companion object {

        @Provides
        fun providePodcastDao(db: AppDatabase) = db.podcastDao()

        @Provides
        fun providePodcastArtistDao(db: AppDatabase) = db.podcastArtistDao()

        @Provides
        fun providePodcastAlbumDao(db: AppDatabase) = db.podcastAlbumDao()

        @Provides
        internal fun providePodcastPositionDao(db: AppDatabase) = db.podcastPositionDao()

    }

}