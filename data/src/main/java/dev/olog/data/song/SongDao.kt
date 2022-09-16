package dev.olog.data.song

import androidx.room.Dao
import androidx.room.Query
import dev.olog.data.mediastore.song.MediaStoreSongsView
import dev.olog.data.mediastore.song.MediaStoreSongsViewSorted
import kotlinx.coroutines.flow.Flow

@Dao
abstract class SongDao {

    @Query("SELECT * FROM songs_view_sorted")
    // todo made suspend
    abstract fun getAll(): List<MediaStoreSongsViewSorted>

    @Query("SELECT * FROM songs_view_sorted")
    abstract fun observeAll(): Flow<List<MediaStoreSongsViewSorted>>

    @Query("SELECT * FROM songs_view WHERE id = :id")
    // todo made suspend
    abstract fun getById(id: String): MediaStoreSongsView?

    @Query("SELECT * FROM songs_view WHERE id = :id")
    // seems that is not emitting null on missing item
    abstract fun observeById(id: String): Flow<MediaStoreSongsView?>

    @Query("SELECT * FROM songs_view WHERE displayName = :displayName")
    // todo made suspend
    abstract fun getByDisplayName(displayName: String): MediaStoreSongsView?

    @Query("SELECT * FROM songs_view WHERE albumId = :albumId")
    // todo made suspend
    abstract fun getByAlbumId(albumId: String): MediaStoreSongsView?

    @Query("SELECT COUNT(*) FROM songs_view")
    abstract fun countAll(): Int

    @Query("SELECT COUNT(*) FROM songs_view")
    abstract fun observeCountAll(): Flow<Int>

}