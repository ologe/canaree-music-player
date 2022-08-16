package dev.olog.data.db.favorite

import androidx.room.*
import dev.olog.core.entity.favorite.FavoriteType
import kotlinx.coroutines.flow.Flow

@Dao
abstract class FavoriteDao {

    @Query("SELECT songId FROM favorite_songs")
    abstract fun getAllTracksImpl(): List<Long>

    @Query("SELECT podcastId FROM favorite_podcast_songs")
    abstract fun getAllPodcastsImpl(): List<Long>

    @Query("SELECT songId FROM favorite_songs")
    abstract fun observeAllTracksImpl(): Flow<List<Long>>

    @Query("SELECT podcastId FROM favorite_podcast_songs")
    abstract fun observeAllPodcastsImpl(): Flow<List<Long>>

    @Query("DELETE FROM favorite_songs")
    abstract fun deleteTracks()

    @Query("DELETE FROM favorite_podcast_songs")
    abstract fun deleteAllPodcasts()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertOneImpl(item: FavoriteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertOnePodcastImpl(item: FavoritePodcastEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertGroupImpl(item: List<FavoriteEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertGroupPodcastImpl(item: List<FavoritePodcastEntity>)

    @Delete
    abstract suspend fun deleteGroupImpl(item: List<FavoriteEntity>)

    @Delete
    abstract suspend fun deleteGroupPodcastImpl(item: List<FavoritePodcastEntity>)

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

    @Query("SELECT songId FROM favorite_songs WHERE songId = :songId")
    abstract suspend fun isFavorite(songId: Long): FavoriteEntity?

    @Query("SELECT podcastId FROM favorite_podcast_songs WHERE podcastId = :podcastId")
    abstract suspend fun isFavoritePodcast(podcastId: Long): FavoritePodcastEntity?

}