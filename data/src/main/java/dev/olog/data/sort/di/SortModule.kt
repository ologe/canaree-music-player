package dev.olog.data.sort.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.olog.core.prefs.SortPreferences
import dev.olog.data.db.AppDatabase
import dev.olog.data.prefs.sort.AppSortingImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SortModule {

    @Binds
    @Singleton
    internal abstract fun provideSortPreferences(impl: AppSortingImpl): SortPreferences

    companion object {

        @Provides
        fun provideSortDao(db: AppDatabase) = db.sortDao()

    }

}