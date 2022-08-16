package dev.olog.data.db.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration15to16 : Migration(15, 16) {

    override fun migrate(database: SupportSQLiteDatabase) {
        dropLastFmTables(database)
        createLastFm2Tables(database)
        createImageVersionTable(database)
        createLyricsSyncAdjustmentTables(database)
        createEqualizerPresetTables(database)
        createBrandNewUsedImageTables(database)
        createPlaylistTables(database)
    }

    /**
     * drops last_fm tables and mini_queue
     */
    private fun dropLastFmTables(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE last_fm_podcast")
        database.execSQL("DROP TABLE last_fm_podcast_album")
        database.execSQL("DROP TABLE last_fm_podcast_artist")

        database.execSQL("DROP TABLE last_fm_track")
        database.execSQL("DROP TABLE last_fm_album")
        database.execSQL("DROP TABLE last_fm_artist")

        database.execSQL("DROP TABLE mini_queue")
    }

    /**
     * creates the same tables with mbid and wiki columns
     */
    private fun createLastFm2Tables(database: SupportSQLiteDatabase) {
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

    /**
     * creates image version table
     */
    private fun createImageVersionTable(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
                CREATE TABLE IF NOT EXISTS `image_version` (`mediaId` TEXT NOT NULL, `version` INTEGER NOT NULL, `maxVersionReached` INTEGER NOT NULL,  PRIMARY KEY(`mediaId`))
            """
        )
        database.execSQL("CREATE  INDEX `index_image_version_mediaId` ON `image_version` (`mediaId`)")
    }

    /**
     * create lyrics sync adustment table
     */
    private fun createLyricsSyncAdjustmentTables(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
                CREATE TABLE IF NOT EXISTS `lyrics_sync_adjustment` (`id` INTEGER NOT NULL, `millis` INTEGER NOT NULL, PRIMARY KEY(`id`))
            """
        )
        database.execSQL(
            """
                CREATE  INDEX `index_lyrics_sync_adjustment_id` ON `lyrics_sync_adjustment` (`id`)
            """
        )
    }

    private fun createEqualizerPresetTables(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
                CREATE TABLE IF NOT EXISTS `equalizer_preset` (`id` INTEGER NOT NULL, `name` TEXT NOT NULL, `bands` TEXT NOT NULL, `isCustom` INTEGER NOT NULL, PRIMARY KEY(`id`))
            """
        )
        database.execSQL(
            """
                CREATE  INDEX `index_equalizer_preset_id` ON `equalizer_preset` (`id`)
            """
        )
    }

    private fun createBrandNewUsedImageTables(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE used_image_track")
        database.execSQL("DROP TABLE used_image_album")
        database.execSQL("DROP TABLE used_image_artist")

        database.execSQL(
            """
                CREATE TABLE IF NOT EXISTS `used_image_track_2` (`id` INTEGER NOT NULL, `image` TEXT NOT NULL, PRIMARY KEY(`id`))
            """
        )
        database.execSQL(
            """
                CREATE TABLE IF NOT EXISTS `used_image_album_2` (`id` INTEGER NOT NULL, `image` TEXT NOT NULL, PRIMARY KEY(`id`))
            """
        )
        database.execSQL(
            """
                CREATE TABLE IF NOT EXISTS `used_image_artist_2` (`id` INTEGER NOT NULL, `image` TEXT NOT NULL, PRIMARY KEY(`id`))
            """
        )
    }

    private fun createPlaylistTables(database: SupportSQLiteDatabase) {

        database.execSQL(
            """
                CREATE TABLE IF NOT EXISTS `playlist` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `size` INTEGER NOT NULL)
            """
        )
        database.execSQL(
            """
                CREATE TABLE IF NOT EXISTS `playlist_tracks` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `idInPlaylist` INTEGER NOT NULL, `trackId` INTEGER NOT NULL, `playlistId` INTEGER NOT NULL, FOREIGN KEY(`playlistId`) REFERENCES `playlist`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )
            """
        )
        database.execSQL(
            """
                CREATE  INDEX `index_playlist_tracks_playlistId` ON `playlist_tracks` (`playlistId`)
            """
        )
    }

}