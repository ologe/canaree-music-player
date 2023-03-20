package dev.olog.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.olog.data.db.entities.FolderMostPlayedEntity
import dev.olog.data.mediastore.MediaStoreAudioView
import dev.olog.data.queries.QueryUtils
import kotlinx.coroutines.flow.Flow

@Dao
internal abstract class FolderMostPlayedDao {

    @Query("""
        SELECT mediastore_audio.*
        FROM most_played_folder JOIN mediastore_audio 
            ON most_played_folder.songId = mediastore_audio._id
        WHERE most_played_folder.folderId = :folderId
        GROUP BY most_played_folder.songId
        HAVING count(*) >= ${QueryUtils.MOST_PLAYED_HAVE_AT_LEAST}
        ORDER BY count(*) DESC
        LIMIT ${QueryUtils.MOST_PLAYED_LIMIT}
    """)
    abstract fun observe(folderId: Long): Flow<List<MediaStoreAudioView>>

    @Insert
    abstract suspend fun insertOne(item: FolderMostPlayedEntity)

}
