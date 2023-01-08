package dev.olog.data

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.olog.data.db.dao.AppDatabase
import dev.olog.data.migrations.Migration15to16
import dev.olog.data.migrations.Migration16to17
import dev.olog.data.migrations.Migration17to18
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryHelperModule {

    @Provides
    @Singleton
    internal fun provideRoomDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "db")
            .addMigrations(
                Migration15to16(),
                Migration16to17(),
                Migration17to18()
            )
            .allowMainThreadQueries()
            .build()
    }

}