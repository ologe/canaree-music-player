package dev.olog.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.olog.data.db.dao.AppDatabase
import dev.olog.data.migrations.BlacklistMigration
import dev.olog.data.migrations.Migration15to16
import dev.olog.data.migrations.Migration16to17
import dev.olog.data.migrations.Migration17to18
import dev.olog.data.migrations.Migration18to19
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryHelperModule {

    @Provides
    @Singleton
    internal fun provideRoomDatabase(
        @ApplicationContext context: Context,
        blacklistMigration: BlacklistMigration,
    ): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "db")
            .addMigrations(
                Migration15to16(),
                Migration16to17(),
                Migration17to18(),
                Migration18to19(),
            )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    blacklistMigration.migrate(db)
                }
            })
            .allowMainThreadQueries()
            .build()
    }

}