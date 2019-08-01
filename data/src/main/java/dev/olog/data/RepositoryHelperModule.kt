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
            .addMigrations(
                MIGRATION_15_16,
                MIGRATION_16_17,
                MIGRATION_17_18,
                MIGRATION_18_19,
                MIGRATION_19_20
            )
            .build()
    }

    /**
     * drops last_fm_podcast tables and mini_queue
     */
    private val MIGRATION_15_16 = object : Migration(15, 16) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("DROP TABLE last_fm_podcast")
            database.execSQL("DROP TABLE last_fm_podcast_album")
            database.execSQL("DROP TABLE last_fm_podcast_artist")
            database.execSQL("DROP TABLE mini_queue")
        }
    }

    /**
     * drop last_fm_track tables
     * creates the same tables with mbid and wiki columns
     */
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

    /**
     * creates image version table
     */
    private val MIGRATION_17_18 = object : Migration(17, 18){
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS `image_version` (`mediaId` TEXT NOT NULL, `version` INTEGER NOT NULL, PRIMARY KEY(`mediaId`))
            """.trimIndent())
            database.execSQL("CREATE  INDEX `index_image_version_mediaId` ON `image_version` (`mediaId`)")
        }
    }

    /**
     * create lyrics sync adustment table
     */
    private val MIGRATION_18_19 = object : Migration(18, 19){
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS `lyrics_sync_adjustment` (`id` INTEGER NOT NULL, `millis` INTEGER NOT NULL, PRIMARY KEY(`id`))
            """.trimIndent())
            database.execSQL("""
                CREATE  INDEX `index_lyrics_sync_adjustment_id` ON `lyrics_sync_adjustment` (`id`)
            """.trimIndent())
        }
    }

    private val MIGRATION_19_20 = object : Migration(19, 20){
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS `equalizer_preset` (`id` INTEGER NOT NULL, `name` TEXT NOT NULL, `bands` TEXT NOT NULL, `isCustom` INTEGER NOT NULL, PRIMARY KEY(`id`))
            """.trimIndent())
            database.execSQL("""
                CREATE  INDEX `index_equalizer_preset_id` ON `equalizer_preset` (`id`)
            """.trimIndent())
        }
    }

}