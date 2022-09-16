package dev.olog.data.favourites

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.olog.data.mediastore.podcast.MediaStorePodcastsView
import dev.olog.data.mediastore.song.MediaStoreSongsView
import kotlinx.coroutines.flow.Flow

@Dao
abstract class FavoriteDao {

    @Query("""
        SELECT songs_view.* 
        FROM favourites JOIN songs_view ON favourites.id = songs_view.id 
    """)
    abstract fun getAllTracks(): List<MediaStoreSongsView>

    @Query("""
        SELECT podcasts_view.* 
        FROM favourites JOIN podcasts_view ON favourites.id = podcasts_view.id
    """)
    abstract fun getAllPodcasts(): List<MediaStorePodcastsView>

    @Query("""
        SELECT songs_view.* 
        FROM favourites JOIN songs_view ON favourites.id = songs_view.id
    """)
    abstract fun observeAllTracks(): Flow<List<MediaStoreSongsView>>

    @Query("""
        SELECT podcasts_view.* 
        FROM favourites JOIN podcasts_view ON favourites.id = podcasts_view.id
    """)
    abstract fun observeAllPodcasts(): Flow<List<MediaStorePodcastsView>>

    @Query("""
        DELETE FROM favourites 
        WHERE favourites.id IN 
            (SELECT id FROM songs_view)
    """)
    abstract fun deleteAllTracks()

    @Query("""
        DELETE FROM favourites 
        WHERE favourites.id IN 
            (SELECT id FROM podcasts_view)
    """)
    abstract fun deleteAllPodcasts()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(item: FavoriteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(item: List<FavoriteEntity>)

    @Delete
    abstract suspend fun delete(item: FavoriteEntity)

    @Delete
    abstract suspend fun delete(item: List<FavoriteEntity>)

    @Query("SELECT id FROM favourites WHERE id = :id")
    abstract fun isFavourite(id: String): Flow<FavoriteEntity?>

    @Query("""
        SELECT COUNT(*) 
        FROM favourites JOIN songs_view ON favourites.id = songs_view.id
    """)
    abstract fun countAllSongs(): Int

    @Query("""
        SELECT COUNT(*) 
        FROM favourites JOIN podcasts_view ON favourites.id = podcasts_view.id
    """)
    abstract fun countAllPodcasts(): Int

    @Query("""
        SELECT COUNT(*) 
        FROM favourites JOIN songs_view ON favourites.id = songs_view.id
    """)
    abstract fun observeCountAllSongs(): Flow<Int>

    @Query("""
        SELECT COUNT(*) 
        FROM favourites JOIN podcasts_view ON favourites.id = podcasts_view.id
    """)
    abstract fun observeCountAllPodcasts(): Flow<Int>

}