package dev.olog.data.db

import androidx.annotation.WorkerThread
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.olog.data.mediastore.playlist.MediaStorePlaylistDirectoryEntity
import javax.inject.Inject

class PlaylistMigration @Inject constructor(

) {

    @WorkerThread
    fun migrate(db: SupportSQLiteDatabase) {
        val id = MediaStorePlaylistDirectoryEntity.createId()
        db.execSQL("INSERT OR IGNORE INTO playlist_directory VALUES(${id}, null, null)")
    }

}