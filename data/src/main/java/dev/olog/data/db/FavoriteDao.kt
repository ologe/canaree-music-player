package dev.olog.data.db

import androidx.room.*
import dev.olog.data.model.db.FavoriteEntity
import dev.olog.data.model.db.FavoritePodcastEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal abstract class FavoriteDao {

    @Query("SELECT songId FROM favorite_songs ORDER BY songId")
    abstract fun getAllTracksImpl(): List<Long>

    @Query("SELECT podcastId FROM favorite_podcast_songs ORDER BY podcastId")
    abstract fun getAllPodcastsImpl(): List<Long>

    @Query("SELECT songId FROM favorite_songs ORDER BY songId")
    abstract fun observeAllTracksImpl(): Flow<List<Long>>

    @Query("SELECT podcastId FROM favorite_podcast_songs ORDER BY podcastId")
    abstract fun observeAllPodcastsImpl(): Flow<List<Long>>

    @Query("DELETE FROM favorite_songs")
    abstract fun deleteAllTracks()

    @Query("DELETE FROM favorite_podcast_songs")
    abstract fun deleteAllPodcasts()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertOneImpl(item: FavoriteEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertOnePodcastImpl(item: FavoritePodcastEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertGroupImpl(item: List<FavoriteEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertGroupPodcastImpl(item: List<FavoritePodcastEntity>): List<Long>

    @Delete
    abstract suspend fun deleteGroupImpl(item: List<FavoriteEntity>): Int

    @Delete
    abstract suspend fun deleteGroupPodcastImpl(item: List<FavoritePodcastEntity>): Int

    @Query("SELECT songId FROM favorite_songs WHERE songId = :songId")
    abstract suspend fun getTrackById(songId: Long): FavoriteEntity?

    @Query("SELECT podcastId FROM favorite_podcast_songs WHERE podcastId = :podcastId")
    abstract suspend fun getPodcastById(podcastId: Long): FavoritePodcastEntity?

    suspend fun isFavorite(songId: Long): Boolean {
        return getTrackById(songId) != null
    }

    suspend fun isFavoritePodcast(podcastId: Long): Boolean {
        return getPodcastById(podcastId) != null
    }

}