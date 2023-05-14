package dev.olog.data.db.dao

import androidx.room.*
import dev.olog.core.entity.favorite.FavoriteType
import dev.olog.data.db.entities.FavoriteEntity
import dev.olog.data.db.entities.FavoritePodcastEntity
import dev.olog.data.mediastore.audio.MediaStoreAudioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("""
        SELECT mediastore_audio.*
        FROM favorite_songs JOIN mediastore_audio
            ON favorite_songs.songId = mediastore_audio._id
        ORDER BY title
    """)
    fun getAllTracks(): List<MediaStoreAudioEntity>

    @Query("""
        SELECT mediastore_audio.*
        FROM favorite_podcast_songs JOIN mediastore_audio
            ON favorite_podcast_songs.podcastId = mediastore_audio._id
        ORDER BY title
    """)
    fun getAllPodcasts(): List<MediaStoreAudioEntity>

    @Query("""
        SELECT mediastore_audio.*
        FROM favorite_songs JOIN mediastore_audio
            ON favorite_songs.songId = mediastore_audio._id
        ORDER BY title
    """)
    fun observeAllTracks(): Flow<List<MediaStoreAudioEntity>>

    @Query("""
        SELECT mediastore_audio.*
        FROM favorite_podcast_songs JOIN mediastore_audio
            ON favorite_podcast_songs.podcastId = mediastore_audio._id
        ORDER BY title
    """)
    fun observeAllPodcasts(): Flow<List<MediaStoreAudioEntity>>

    @Query("DELETE FROM favorite_songs")
    suspend fun deleteTracks()

    @Query("DELETE FROM favorite_podcast_songs")
    suspend fun deleteAllPodcasts()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOneImpl(item: FavoriteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOnePodcastImpl(item: FavoritePodcastEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupImpl(item: List<FavoriteEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupPodcastImpl(item: List<FavoritePodcastEntity>)

    @Delete
    suspend fun deleteGroupImpl(item: List<FavoriteEntity>)

    @Delete
    suspend fun deleteGroupPodcastImpl(item: List<FavoritePodcastEntity>)

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

    suspend fun removeFromFavorite(type: FavoriteType, songId: List<Long>) {
        if (type == FavoriteType.TRACK){
            deleteGroupImpl(songId.map { FavoriteEntity(it) })
        } else {
            deleteGroupPodcastImpl(songId.map { FavoritePodcastEntity(it) })
        }
    }

    @Query("SELECT songId FROM favorite_songs WHERE songId = :songId")
    suspend fun isFavorite(songId: Long): FavoriteEntity?

    @Query("SELECT podcastId FROM favorite_podcast_songs WHERE podcastId = :podcastId")
    suspend fun isFavoritePodcast(podcastId: Long): FavoritePodcastEntity?

}