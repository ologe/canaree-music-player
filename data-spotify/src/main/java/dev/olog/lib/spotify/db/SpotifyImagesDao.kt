package dev.olog.lib.spotify.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
abstract class SpotifyImagesDao {

    @Query("SELECT image from spotify_images WHERE uri = :spotifyUri")
    abstract fun getImage(spotifyUri: String): String?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertImages(entities: List<SpotifyImageEntity>)

}