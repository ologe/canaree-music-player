package dev.olog.data.db.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration17To18 : Migration(17, 18){
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE image_version")
        database.execSQL("DROP TABLE used_image_track_2")
        database.execSQL("DROP TABLE used_image_album_2")
        database.execSQL("DROP TABLE used_image_artist_2")
    }
}