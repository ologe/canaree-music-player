package dev.olog.data.song

import androidx.room.Dao
import androidx.room.Query
import dev.olog.data.mediastore.MediaStoreSongView
import dev.olog.data.mediastore.MediaStoreSortedSongView
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SongDao {

    @Query("SELECT * FROM sorted_songs_view")
    // todo made suspend
    abstract fun getAll(): List<MediaStoreSortedSongView>

    @Query("SELECT * FROM sorted_songs_view")
    abstract fun observeAll(): Flow<List<MediaStoreSortedSongView>>

    @Query("SELECT * FROM songs_view WHERE id = :id")
    // todo made suspend
    abstract fun getById(id: String): MediaStoreSongView?

    @Query("SELECT * FROM songs_view WHERE id = :id")
    // seems that is not emitting null on missing item
    abstract fun observeById(id: String): Flow<MediaStoreSongView?>

    @Query("SELECT * FROM songs_view WHERE displayName = :displayName")
    // todo made suspend
    abstract fun getByDisplayName(displayName: String): MediaStoreSongView?

    @Query("SELECT * FROM songs_view WHERE albumId = :albumId")
    // todo made suspend
    abstract fun getByAlbumId(albumId: String): MediaStoreSongView?
}