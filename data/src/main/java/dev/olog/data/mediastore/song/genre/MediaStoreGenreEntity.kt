package dev.olog.data.mediastore.song.genre

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "mediastore_genre",
    indices = [
        Index("id")
    ]
)
data class MediaStoreGenreEntity(
    @PrimaryKey
    val id: String,
    val name: String,
)

@Entity(
    tableName = "mediastore_genre_track",
    indices = [
        Index("genreId"),
        Index("songId"),
    ],
    primaryKeys = ["genreId", "songId"]
)
data class MediaStoreGenreTrackEntity(
    val genreId: String,
    val songId: String,
)