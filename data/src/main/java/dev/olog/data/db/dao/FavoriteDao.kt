package dev.olog.data.db.dao

import androidx.room.*
import dev.olog.core.entity.favorite.FavoriteType
import dev.olog.data.db.entities.FavoriteEntity
import dev.olog.data.db.entities.FavoritePodcastEntity
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
    abstract fun deleteTracks()

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

    suspend fun addToFavoriteSingle(type: FavoriteType, id: Long) {
        if (type == FavoriteType.TRACK) {
            insertOneImpl(FavoriteEntity(id))
        } else {
            insertOnePodcastImpl(FavoritePodcastEntity(id))
        }
    }

    suspend fun addToFavorite(type: FavoriteType, songIds: List<Long>) {
        if (type == FavoriteType.TRACK) {
            insertGroupImpl(songIds.map { FavoriteEntity(it) })
        } else {
            insertGroupPodcastImpl(songIds.map { FavoritePodcastEntity(it) })
        }
    }

    open suspend fun removeFromFavorite(type: FavoriteType, songId: List<Long>) {
        if (type == FavoriteType.TRACK){
            deleteGroupImpl(songId.map { FavoriteEntity(it) })
        } else {
            deleteGroupPodcastImpl(songId.map { FavoritePodcastEntity(it) })
        }
    }

}