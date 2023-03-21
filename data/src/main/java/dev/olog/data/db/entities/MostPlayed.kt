package dev.olog.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "most_played_folder",
    indices = [(Index("id"))]
)
data class FolderMostPlayedEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val songId: Long,
    val folderId: Long,
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
    val id: Long = 0,
    val songId: Long,
    val genreId: Long
)