package dev.olog.data.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration18to19 : Migration(18, 19) {

    override fun migrate(database: SupportSQLiteDatabase) {
        // mediastore audio
        database.execSQL("CREATE TABLE IF NOT EXISTS `mediastore_audio` (`id` TEXT NOT NULL, `artistId` TEXT NOT NULL, `albumId` TEXT NOT NULL, `title` TEXT NOT NULL, `artist` TEXT NOT NULL, `albumArtist` TEXT NOT NULL, `album` TEXT NOT NULL, `duration` INTEGER NOT NULL, `dateAdded` INTEGER NOT NULL, `dateModified` INTEGER NOT NULL, `path` TEXT NOT NULL, `directoryPath` TEXT NOT NULL, `directoryName` TEXT NOT NULL, `discNumber` INTEGER NOT NULL, `trackNumber` INTEGER NOT NULL, `isPodcast` INTEGER NOT NULL, `displayName` TEXT NOT NULL, PRIMARY KEY(`id`))")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_mediastore_audio_id` ON `mediastore_audio` (`id`)")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_mediastore_audio_artistId` ON `mediastore_audio` (`artistId`)")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_mediastore_audio_albumId` ON `mediastore_audio` (`albumId`)")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_mediastore_audio_path` ON `mediastore_audio` (`path`)")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_mediastore_audio_directoryPath` ON `mediastore_audio` (`directoryPath`)")

        database.execSQL("CREATE VIEW `mediastore_view` AS SELECT mediastore_audio.*\nFROM mediastore_audio\n    LEFT JOIN blacklist ON mediastore_audio.directoryPath = blacklist.directory\nWHERE blacklist.directory IS NULL")

        // blacklist
        database.execSQL("CREATE TABLE IF NOT EXISTS `blacklist` (`directory` TEXT NOT NULL, PRIMARY KEY(`directory`))")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_blacklist_directory` ON `blacklist` (`directory`)")
    }
}