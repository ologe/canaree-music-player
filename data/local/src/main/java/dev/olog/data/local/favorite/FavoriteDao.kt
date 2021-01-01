package dev.olog.data.local.favorite

import androidx.room.*
import dev.olog.domain.entity.Favorite
import dev.olog.shared.exhaustive
import kotlinx.coroutines.flow.Flow

@Dao
abstract class FavoriteDao {

    @Query("SELECT songId FROM favorite_songs")
    abstract suspend fun getAllTracksImpl(): List<Long>

    @Query("SELECT podcastId FROM favorite_podcast_songs")
    abstract suspend fun getAllPodcastsImpl(): List<Long>

    @Query("SELECT songId FROM favorite_songs")
    abstract fun observeAllTracksImpl(): Flow<List<Long>>

    @Query("SELECT podcastId FROM favorite_podcast_songs")
    abstract fun observeAllPodcastsImpl(): Flow<List<Long>>

    @Query("DELETE FROM favorite_songs")
    abstract suspend fun deleteTracks()

    @Query("DELETE FROM favorite_podcast_songs")
    abstract suspend fun deleteAllPodcasts()

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

    suspend fun addToFavoriteSingle(type: Favorite.Type, id: Long) {
        when (type) {
            Favorite.Type.TRACK -> insertOneImpl(FavoriteEntity(id))
            Favorite.Type.PODCAST -> insertOnePodcastImpl(FavoritePodcastEntity(id))
        }.exhaustive
    }

    suspend fun addToFavorite(type: Favorite.Type, songIds: List<Long>) {
        when (type) {
            Favorite.Type.TRACK -> insertGroupImpl(songIds.map { FavoriteEntity(it) })
            Favorite.Type.PODCAST -> insertGroupPodcastImpl(songIds.map { FavoritePodcastEntity(it) })
        }.exhaustive
    }

    open suspend fun removeFromFavorite(type: Favorite.Type, songId: List<Long>) {
        when (type) {
            Favorite.Type.TRACK -> deleteGroupImpl(songId.map { FavoriteEntity(it) })
            Favorite.Type.PODCAST -> deleteGroupPodcastImpl(songId.map { FavoritePodcastEntity(it) })
        }.exhaustive
    }

    @Query("SELECT songId FROM favorite_songs WHERE songId = :songId")
    abstract suspend fun isFavorite(songId: Long): FavoriteEntity?

    @Query("SELECT podcastId FROM favorite_podcast_songs WHERE podcastId = :podcastId")
    abstract suspend fun isFavoritePodcast(podcastId: Long): FavoritePodcastEntity?

}