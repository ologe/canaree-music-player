package dev.olog.data.model.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "most_played_folder",
    indices = [(Index("id"))]
)
data class FolderMostPlayedEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val songId: Long,
    val folderPath: String
)

@Entity(
    tableName = "most_played_playlist",
    indices = [(Index("id"))]
)
data class PlaylistMostPlayedEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val songId: Long,
    val playlistId: Long
)

@Entity(
    tableName = "most_played_genre",
    indices = [(Index("id"))]
)
data class GenreMostPlayedEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val songId: Long,
    val genreId: Long
)