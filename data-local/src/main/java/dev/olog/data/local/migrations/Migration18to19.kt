package dev.olog.data.local.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

internal class Migration18to19 : Migration(18, 19) {

    override fun migrate(database: SupportSQLiteDatabase) {
        updatePlayingQueueTable(database)
    }

    // delete "category" and "categoryValue" column (using workaround, sqlite don't support this)
    // rename progressive to internalId
    // rename idInPlaylist to serviceProgressive
    private fun updatePlayingQueueTable(database: SupportSQLiteDatabase) {
        database.execSQL("BEGIN TRANSACTION")
        // create new table
        database.execSQL("""
            CREATE TABLE `playing_queue_2` (
                progressive INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                songId INTEGER NOT NULL, 
                idInPlaylist INTEGER NOT NULL
            )
        """)

        // copy data to new table
        database.execSQL("""
            INSERT INTO playing_queue_2(progressive, songId, idInPlaylist)
            SELECT progressive, songId, idInPlaylist
            FROM playing_queue
        """)

        // delete old table and rename new the table
        database.execSQL("DROP TABLE playing_queue")
        database.execSQL("ALTER TABLE playing_queue_2 RENAME TO playing_queue")

        // rename progressive to internalId
        database.execSQL("ALTER TABLE playing_queue RENAME COLUMN progressive to internalId")
        // rename idInPlaylist to serviceProgressive
        database.execSQL("ALTER TABLE playing_queue RENAME COLUMN idInPlaylist to serviceProgressive")

        // delete old index if exists and create new index
        database.execSQL("DROP INDEX IF EXISTS index_playing_queue_progressive")
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_playing_queue_progressive` ON playing_queue (`internalId`)")

        database.execSQL("COMMIT")
    }

}