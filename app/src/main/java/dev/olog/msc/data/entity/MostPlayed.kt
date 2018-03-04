package dev.olog.msc.data.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "mostplayedfolder",
        indices = arrayOf(Index("id"))
)
data class FolderMostPlayedEntity(
        @PrimaryKey(autoGenerate = true) val id: Long,
        val songId: Long,
        val folderPath: String
)

@Entity(tableName = "mostplayedplaylist",
        indices = arrayOf(Index("id"))
)
data class PlaylistMostPlayedEntity(
        @PrimaryKey(autoGenerate = true) val id: Long,
        val songId: Long,
        val playlistId: Long
)

@Entity(tableName = "mostplayedgenre",
        indices = arrayOf(Index("id"))
)
data class GenreMostPlayedEntity(
        @PrimaryKey(autoGenerate = true) val id: Long,
        val songId: Long,
        val genreId: Long
)