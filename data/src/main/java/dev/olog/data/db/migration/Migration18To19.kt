package dev.olog.data.db.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration18To19 : Migration(18, 19) {

    override fun migrate(database: SupportSQLiteDatabase) {
        // mediastore audio internal
        database.execSQL("CREATE TABLE IF NOT EXISTS `mediastore_audio_internal` (`_id` INTEGER NOT NULL, `album_id` INTEGER NOT NULL, `artist_id` INTEGER NOT NULL, `title` TEXT NOT NULL COLLATE LOCALIZED, `album` TEXT COLLATE LOCALIZED, `album_artist` TEXT COLLATE LOCALIZED, `artist` TEXT COLLATE LOCALIZED, `bucket_id` INTEGER NOT NULL, `bucket_display_name` TEXT NOT NULL COLLATE UNICODE, `_data` TEXT, `relative_path` TEXT NOT NULL, `_display_name` TEXT NOT NULL, `is_podcast` INTEGER NOT NULL, `bookmark` INTEGER, `duration` INTEGER NOT NULL, `author` TEXT, `bitrate` INTEGER NOT NULL, `compilation` TEXT, `composer` TEXT, `_size` INTEGER NOT NULL, `track` INTEGER, `year` INTEGER, `writer` TEXT, `is_favorite` INTEGER NOT NULL, `date_added` INTEGER NOT NULL, PRIMARY KEY(`_id`))")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_mediastore_audio_internal__id` ON `mediastore_audio_internal` (`_id`)")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_mediastore_audio_internal_album_id` ON `mediastore_audio_internal` (`album_id`)")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_mediastore_audio_internal_artist_id` ON `mediastore_audio_internal` (`artist_id`)")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_mediastore_audio_internal_bucket_id` ON `mediastore_audio_internal` (`bucket_id`)")

        // mediastore audio view
        database.execSQL("CREATE VIEW `mediastore_audio` AS SELECT mediastore_audio_internal.*\nFROM mediastore_audio_internal LEFT JOIN blacklist \n    ON mediastore_audio_internal.relative_path = blacklist.directory\nWHERE blacklist.directory IS NULL")

        // mediastore folders view
        database.execSQL("CREATE VIEW `mediastore_folders` AS SELECT bucket_id, bucket_display_name, relative_path, count(*) as size\nFROM mediastore_audio\nGROUP BY bucket_id")
        // recreate most_played_folder
        database.execSQL("DROP TABLE IF EXISTS `most_played_folder`")
        database.execSQL("CREATE TABLE IF NOT EXISTS `most_played_folder` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `songId` INTEGER NOT NULL, `folderId` INTEGER NOT NULL)")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_most_played_folder_id` ON `most_played_folder` (`id`)")

        // mediastore artist view
        database.execSQL("CREATE VIEW `mediastore_artists` AS SELECT artist_id, artist, album_artist, is_podcast, count(*) as size\nFROM mediastore_audio\nGROUP BY artist_id")

        // blacklist
        database.execSQL("CREATE TABLE IF NOT EXISTS `blacklist` (`directory` TEXT NOT NULL, PRIMARY KEY(`directory`))")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_blacklist_directory` ON `blacklist` (`directory`)")
    }
}