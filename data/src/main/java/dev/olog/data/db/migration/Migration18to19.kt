package dev.olog.data.db.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration18to19 : Migration(18, 19) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
                CREATE TABLE IF NOT EXISTS mediastore_audio(
                    id TEXT NOT NULL,
                    artistId TEXT NOT NULL,
                    albumId TEXT NOT NULL,
                    title TEXT NOT NULL,
                    artist TEXT NOT NULL,
                    albumArtist TEXT NOT NULL,
                    album TEXT NOT NULL,
                    duration INTEGER NOT NULL,
                    dateAdded INTEGER NOT NULL,
                    dateModified INTEGER NOT NULL,
                    directory TEXT NOT NULL,
                    path TEXT NOT NULL,
                    discNumber INTEGER NOT NULL,
                    trackNumber INTEGER NOT NULL,
                    isPodcast INTEGER NOT NULL,
                    PRIMARY KEY(id)
                )
            """.trimIndent()
        )
    }
}