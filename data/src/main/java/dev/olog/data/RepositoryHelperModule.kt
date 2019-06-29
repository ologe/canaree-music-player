package dev.olog.data

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dev.olog.core.dagger.ApplicationContext
import dev.olog.data.db.dao.AppDatabase
import javax.inject.Singleton

@Module
class RepositoryHelperModule {

    @Provides
    @Singleton
    internal fun provideRoomDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "db")
            .addMigrations(MIGRATION_15_16)
            .build()
    }

    private val MIGRATION_15_16 = object : Migration(15, 16) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("DROP TABLE last_fm_podcast")
            database.execSQL("DROP TABLE last_fm_podcast_album")
            database.execSQL("DROP TABLE last_fm_podcast_artist")
            database.execSQL("DROP TABLE mini_queue")
        }
    }

}