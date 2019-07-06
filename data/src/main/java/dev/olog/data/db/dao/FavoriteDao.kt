package dev.olog.data.db.dao

import androidx.room.*
import dev.olog.data.db.entities.FavoriteEntity
import dev.olog.data.db.entities.FavoritePodcastEntity
import dev.olog.core.entity.favorite.FavoriteType
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
internal abstract class FavoriteDao {

    @Query("SELECT songId FROM favorite_songs")
    abstract fun getAllTracksImpl(): List<Long>

    @Query("SELECT podcastId FROM favorite_podcast_songs")
    abstract fun getAllPodcastsImpl(): List<Long>

    @Query("SELECT songId FROM favorite_songs")
    abstract fun observeAllTracksImpl(): Flowable<List<Long>>

    @Query("SELECT podcastId FROM favorite_podcast_songs")
    abstract fun observeAllPodcastsImpl(): Flowable<List<Long>>

    @Query("DELETE FROM favorite_songs")
    abstract fun deleteTracks()

    @Query("DELETE FROM favorite_podcast_songs")
    abstract fun deleteAllPodcasts()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertOneImpl(item: FavoriteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertOnePodcastImpl(item: FavoritePodcastEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertGroupImpl(item: List<FavoriteEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertGroupPodcastImpl(item: List<FavoritePodcastEntity>)

    @Delete
    abstract fun deleteGroupImpl(item: List<FavoriteEntity>)

    @Delete
    abstract fun deleteGroupPodcastImpl(item: List<FavoritePodcastEntity>)

    fun addToFavoriteSingle(type: FavoriteType, id: Long): Completable {
        return Completable.fromCallable {
            if (type == FavoriteType.TRACK) {
                insertOneImpl(FavoriteEntity(id))
            } else {
                insertOnePodcastImpl(FavoritePodcastEntity(id))
            }
        }
    }

    fun addToFavorite(type: FavoriteType, songIds: List<Long>): Completable {
        return Completable.fromCallable {
            if (type == FavoriteType.TRACK) {
                insertGroupImpl(songIds.map { FavoriteEntity(it) })
            } else {
                insertGroupPodcastImpl(songIds.map { FavoritePodcastEntity(it) })
            }
        }
    }

    open fun removeFromFavorite(type: FavoriteType, songId: List<Long>): Completable {
        return Completable.fromCallable {
            if (type == FavoriteType.TRACK){
                deleteGroupImpl(songId.map { FavoriteEntity(it) })
            } else {
                deleteGroupPodcastImpl(songId.map { FavoritePodcastEntity(it) })
            }
        }
    }

    @Query("SELECT songId FROM favorite_songs WHERE songId = :songId")
    abstract fun isFavorite(songId: Long): FavoriteEntity?

    @Query("SELECT podcastId FROM favorite_podcast_songs WHERE podcastId = :podcastId")
    abstract fun isFavoritePodcast(podcastId: Long): FavoritePodcastEntity?

}