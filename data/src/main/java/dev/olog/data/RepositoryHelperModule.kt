package dev.olog.data

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dev.olog.shared.dagger.ApplicationContext
import dev.olog.data.db.dao.AppDatabase
import javax.inject.Singleton

@Module
class RepositoryHelperModule {

    @Provides
    @Singleton
    internal fun provideRoomDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "db")
            .addMigrations(MIGRATION_15_16, MIGRATION_16_17)
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

    private val MIGRATION_16_17 = object : Migration(16, 17) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("DROP TABLE last_fm_track")
            database.execSQL("DROP TABLE last_fm_album")
            database.execSQL("DROP TABLE last_fm_artist")
            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS last_fm_track_v2 (
                    id INTEGER NOT NULL, 
                    title TEXT NOT NULL, 
                    artist TEXT NOT NULL, 
                    album TEXT NOT NULL, 
                    image TEXT NOT NULL, 
                    added TEXT NOT NULL,
                    mbid TEXT NOT NULL, 
                    artistMbid TEXT NOT NULL, 
                    albumMbid TEXT NOT NULL,
                    PRIMARY KEY(id)
                );
            """
            )
            database.execSQL("CREATE  INDEX `index_last_fm_track_id` ON last_fm_track_v2 (`id`)")
            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS last_fm_album_v2 (
                id INTEGER NOT NULL, 
                title TEXT NOT NULL, 
                artist TEXT NOT NULL, 
                image TEXT NOT NULL, 
                added TEXT NOT NULL, 
                mbid TEXT NOT NULL,
                wiki TEXT NOT NULL,
                PRIMARY KEY(id))
            """
            )
            database.execSQL("CREATE  INDEX `index_last_fm_album_id` ON last_fm_album_v2 (`id`)")
            database.execSQL(
                """
                CREATE TABLE IF NOT EXISTS last_fm_artist_v2 (
                id INTEGER NOT NULL, 
                image TEXT NOT NULL, 
                added TEXT NOT NULL, 
                mbid TEXT NOT NULL, 
                wiki TEXT NOT NULL, 
                PRIMARY KEY(id)
                )
            """
            )
            database.execSQL("CREATE  INDEX `index_last_fm_artist_id` ON last_fm_artist_v2 (`id`)")
        }
    }

}