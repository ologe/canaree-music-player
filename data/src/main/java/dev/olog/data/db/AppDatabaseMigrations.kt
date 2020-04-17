package dev.olog.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

internal object AppDatabaseMigrations {

    val Migration_15_16 = object : Migration(15, 16) {

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
            database.execSQL("DROP TABLE IF EXISTS last_fm_podcast")
            database.execSQL("DROP TABLE IF EXISTS last_fm_podcast_album")
            database.execSQL("DROP TABLE IF EXISTS last_fm_podcast_artist")

            database.execSQL("DROP TABLE IF EXISTS last_fm_track")
            database.execSQL("DROP TABLE IF EXISTS last_fm_album")
            database.execSQL("DROP TABLE IF EXISTS last_fm_artist")

            database.execSQL("DROP TABLE IF EXISTS mini_queue")
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
            database.execSQL("DROP TABLE IF EXISTS used_image_track")
            database.execSQL("DROP TABLE IF EXISTS used_image_album")
            database.execSQL("DROP TABLE IF EXISTS used_image_artist")

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

    val Migration_16_17 = object : Migration(16, 17) {
        override fun migrate(database: SupportSQLiteDatabase) {

        }
    }

    val Migration_17_18 = object : Migration(17, 18) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("DROP TABLE IF EXISTS image_version")
            database.execSQL("DROP TABLE IF EXISTS used_image_track_2")
            database.execSQL("DROP TABLE IF EXISTS used_image_album_2")
            database.execSQL("DROP TABLE IF EXISTS used_image_artist_2")
        }
    }

    val Migration_18_19 = object : Migration(18, 19) {
        override fun migrate(database: SupportSQLiteDatabase) {

            database.execSQL("DROP TABLE IF EXISTS last_played_podcast_albums")
            database.execSQL("DROP TABLE IF EXISTS playing_queue")

            // recreated playing queue with categoryValue as integer
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS playing_queue_2 (
                `progressive` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                `category` TEXT NOT NULL, 
                `categoryValue` TEXT NOT NULL, 
                `songId` INTEGER NOT NULL, 
                `idInPlaylist` INTEGER NOT NULL
                )
            """)
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_playing_queue_2_progressive` ON playing_queue_2 (`progressive`)")

            // recreate recent_searches with `itemId` as TEXT
            database.execSQL("DROP TABLE recent_searches")
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS recent_searches_2 (
                `pk` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                `dataType` INTEGER NOT NULL, 
                `itemId` TEXT NOT NULL, 
                `insertionTime` INTEGER NOT NULL
                )
            """)
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_recent_searches_pk` ON recent_searches_2 (`pk`)")

            // spotify image
            database.execSQL("CREATE TABLE IF NOT EXISTS `spotify_images` (`uri` TEXT NOT NULL, `image` TEXT NOT NULL, PRIMARY KEY(`uri`))")

            // spotify tracks
            database.execSQL("CREATE TABLE IF NOT EXISTS `spotify_tracks` (`localId` INTEGER NOT NULL, `spotifyId` TEXT NOT NULL, `duration_ms` INTEGER NOT NULL, `explicit` INTEGER NOT NULL, `name` TEXT NOT NULL, `popularity` INTEGER, `previewUrl` TEXT, `trackNumber` INTEGER NOT NULL, `discNumber` INTEGER NOT NULL, `uri` TEXT NOT NULL, `image` TEXT NOT NULL, `album` TEXT NOT NULL, `albumId` TEXT NOT NULL, `albumUri` TEXT NOT NULL, `albumType` TEXT NOT NULL, `releaseDate` TEXT NOT NULL, PRIMARY KEY(`localId`))")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_spotify_tracks_localId` ON `spotify_tracks` (`localId`)")

            // spotify tracks audio features
            database.execSQL("CREATE TABLE IF NOT EXISTS `spotify_tracks_audio_feature` (`localId` INTEGER NOT NULL, `spotifyId` TEXT NOT NULL, `uri` TEXT NOT NULL, `acousticness` REAL NOT NULL, `analysis_url` TEXT NOT NULL, `danceability` REAL NOT NULL, `duration_ms` INTEGER NOT NULL, `energy` REAL NOT NULL, `instrumentalness` REAL NOT NULL, `key` INTEGER NOT NULL, `liveness` REAL NOT NULL, `loudness` REAL NOT NULL, `mode` INTEGER NOT NULL, `speechiness` REAL NOT NULL, `tempo` REAL NOT NULL, `track_href` TEXT NOT NULL, `valence` REAL NOT NULL, PRIMARY KEY(`localId`))")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_spotify_tracks_audio_feature_localId` ON `spotify_tracks_audio_feature` (`localId`)")

            // generated playlists
            database.execSQL("CREATE TABLE IF NOT EXISTS `generated_playlist` (`playlistId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `tracks` TEXT NOT NULL)")
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_generated_playlist_playlistId` ON `generated_playlist` (`playlistId`)")
        }
    }

}