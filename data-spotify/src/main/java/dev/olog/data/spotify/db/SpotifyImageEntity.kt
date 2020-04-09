package dev.olog.data.spotify.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spotify_images")
data class SpotifyImageEntity(
    @PrimaryKey val uri: String,
    val image: String
)