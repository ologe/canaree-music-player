package dev.olog.data.mediastore.song.genre

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
abstract class MediaStoreGenreDao {

    @Query("SELECT * FROM mediastore_genre")
    abstract suspend fun getAllGenres(): List<MediaStoreGenreEntity>

    @Query("DELETE FROM mediastore_genre")
    abstract suspend fun deleteAllGenres()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAllGenres(items: List<MediaStoreGenreEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAllGenres(vararg items: MediaStoreGenreEntity)

    @Query("SELECT * FROM mediastore_genre_track")
    abstract suspend fun getAllGenreTracks(): List<MediaStoreGenreTrackEntity>

    @Query("DELETE FROM mediastore_genre_track")
    abstract suspend fun deleteAllGenreTracks()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAllGenreTracks(items: List<MediaStoreGenreTrackEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAllGenreTracks(vararg items: MediaStoreGenreTrackEntity)

    @Transaction
    open suspend fun replaceAll(
        genres: List<MediaStoreGenreEntity>,
        genresTracks: List<MediaStoreGenreTrackEntity>,
    ) {
        deleteAllGenres()
        deleteAllGenreTracks()
        insertAllGenres(genres)
        insertAllGenreTracks(genresTracks)
    }
    
}