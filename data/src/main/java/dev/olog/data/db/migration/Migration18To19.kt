package dev.olog.data.db.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration18To19 : Migration(18, 19) {

    override fun migrate(database: SupportSQLiteDatabase) {
        // mediastore audio
        database.execSQL("CREATE TABLE IF NOT EXISTS `mediastore_audio` (`_id` INTEGER NOT NULL, `album_id` INTEGER NOT NULL, `artist_id` INTEGER NOT NULL, `title` TEXT NOT NULL COLLATE UNICODE, `album` TEXT COLLATE UNICODE, `album_artist` TEXT COLLATE UNICODE, `artist` TEXT COLLATE UNICODE, `bucket_id` INTEGER NOT NULL, `bucket_display_name` TEXT NOT NULL, `_data` TEXT, `relative_path` TEXT NOT NULL, `_display_name` TEXT NOT NULL, `is_alarm` INTEGER NOT NULL, `is_audiobook` INTEGER NOT NULL, `is_music` INTEGER NOT NULL, `is_notification` INTEGER NOT NULL, `is_podcast` INTEGER NOT NULL, `is_recording` INTEGER NOT NULL, `is_ringtone` INTEGER NOT NULL, `bookmark` INTEGER, `duration` INTEGER NOT NULL, `author` TEXT, `bitrate` INTEGER NOT NULL, `compilation` TEXT, `composer` TEXT, `_size` INTEGER NOT NULL, `track` INTEGER, `year` INTEGER, `writer` TEXT, `is_favorite` INTEGER NOT NULL, `date_added` INTEGER NOT NULL, `date_modified` INTEGER NOT NULL, `generation_added` INTEGER NOT NULL, `generation_modified` INTEGER NOT NULL, PRIMARY KEY(`_id`))")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_mediastore_audio__id` ON `mediastore_audio` (`_id`)")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_mediastore_audio_album_id` ON `mediastore_audio` (`album_id`)")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_mediastore_audio_artist_id` ON `mediastore_audio` (`artist_id`)")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_mediastore_audio_bucket_id` ON `mediastore_audio` (`bucket_id`)")
    }
}