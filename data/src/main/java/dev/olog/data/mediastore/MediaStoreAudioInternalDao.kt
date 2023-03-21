package dev.olog.data.mediastore

import android.provider.MediaStore.*
import android.provider.MediaStore.Audio.*
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
abstract class MediaStoreAudioInternalDao {

    @Query("DELETE FROM mediastore_audio_internal")
    abstract suspend fun deleteAll()

    @Insert
    abstract suspend fun insertAll(items: List<MediaStoreAudioInternalEntity>)

    @Transaction
    open suspend fun replaceAll(items: List<MediaStoreAudioInternalEntity>) {
        deleteAll()
        insertAll(items)
    }

    @Query("""
        UPDATE mediastore_audio_internal
        SET genre_id = :genreId, genre = :genre
        WHERE _id = :trackId
    """)
    abstract suspend fun updateGenre(genreId: Long, genre: String, trackId: Long)

    @Transaction
    open suspend fun updateGenres(trackGenres: List<MediaStoreQuery.TrackGenre>) {
        for (item in trackGenres) {
            updateGenre(
                genreId = item.genre.id,
                genre = item.genre.name,
                trackId = item.trackId,
            )
        }
    }

}