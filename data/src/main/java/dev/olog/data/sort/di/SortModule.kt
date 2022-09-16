package dev.olog.data.sort.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.data.db.AppDatabase

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SortModule {

    companion object {

        @Provides
        fun provideSortDao(db: AppDatabase) = db.sortDao()

    }

}