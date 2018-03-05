package dev.olog.msc.data.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "most_played_folder",
        indices = [(Index("id"))]
)
data class FolderMostPlayedEntity(
        @PrimaryKey(autoGenerate = true) val id: Long,
        val songId: Long,
        val folderPath: String
)

@Entity(tableName = "most_played_playlist",
        indices = [(Index("id"))]
)
data class PlaylistMostPlayedEntity(
        @PrimaryKey(autoGenerate = true) val id: Long,
        val songId: Long,
        val playlistId: Long
)

@Entity(tableName = "most_played_genre",
        indices = [(Index("id"))]
)
data class GenreMostPlayedEntity(
        @PrimaryKey(autoGenerate = true) val id: Long,
        val songId: Long,
        val genreId: Long
)