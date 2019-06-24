package dev.olog.data

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dev.olog.core.dagger.ApplicationContext
import dev.olog.data.db.dao.AppDatabase
import javax.inject.Singleton

@Module
class RepositoryHelperModule {

    @Provides
    @Singleton
    fun provideRoomDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "db")
                // TODO migrations
            .build()
    }

}