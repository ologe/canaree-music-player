package dev.olog.data.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.olog.data.db.migration.Migration15to16
import dev.olog.data.db.migration.Migration16to17
import dev.olog.data.db.migration.Migration17to18
import dev.olog.data.db.migration.Migration18to19
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class DatabaseModule {

    @Provides
    @Singleton
    fun provideRoomDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "db")
            .addMigrations(
                Migration15to16(),
                Migration16to17(),
                Migration17to18(),
                Migration18to19(),
            )
            .allowMainThreadQueries() // todo remove
            .build()
    }

}