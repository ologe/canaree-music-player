package dev.olog.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.olog.data.db.entities.GenreMostPlayedEntity
import dev.olog.data.mediastore.audio.MediaStoreAudioEntity
import dev.olog.data.queries.QueryUtils
import kotlinx.coroutines.flow.Flow

@Dao
abstract class GenreMostPlayedDao {

    @Query("""
        SELECT mediastore_audio.*
        FROM most_played_genre JOIN mediastore_audio 
            ON most_played_genre.songId = mediastore_audio._id
        WHERE most_played_genre.genreId = :genreId
        GROUP BY most_played_genre.songId
        HAVING count(*) >= ${QueryUtils.MOST_PLAYED_HAVE_AT_LEAST}
        ORDER BY count(*) DESC
        LIMIT ${QueryUtils.MOST_PLAYED_LIMIT}
    """)
    abstract fun observe(genreId: Long): Flow<List<MediaStoreAudioEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertOne(item: GenreMostPlayedEntity)

}
