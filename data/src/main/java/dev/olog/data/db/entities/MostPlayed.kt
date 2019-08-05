package dev.olog.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "most_played_folder",
    indices = [(Index("id"))]
)
class FolderMostPlayedEntity(
    @PrimaryKey(autoGenerate = true)
    @JvmField
    val id: Long,
    @JvmField
    val songId: Long,
    @JvmField
    val folderPath: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FolderMostPlayedEntity

        if (id != other.id) return false
        if (songId != other.songId) return false
        if (folderPath != other.folderPath) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + songId.hashCode()
        result = 31 * result + folderPath.hashCode()
        return result
    }
}

@Entity(
    tableName = "most_played_playlist",
    indices = [(Index("id"))]
)
class PlaylistMostPlayedEntity(
    @PrimaryKey(autoGenerate = true)
    @JvmField
    val id: Long,
    @JvmField
    val songId: Long,
    @JvmField
    val playlistId: Long
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlaylistMostPlayedEntity

        if (id != other.id) return false
        if (songId != other.songId) return false
        if (playlistId != other.playlistId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + songId.hashCode()
        result = 31 * result + playlistId.hashCode()
        return result
    }
}

@Entity(
    tableName = "most_played_genre",
    indices = [(Index("id"))]
)
class GenreMostPlayedEntity(
    @PrimaryKey(autoGenerate = true)
    @JvmField
    val id: Long,
    @JvmField
    val songId: Long,
    @JvmField
    val genreId: Long
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GenreMostPlayedEntity

        if (id != other.id) return false
        if (songId != other.songId) return false
        if (genreId != other.genreId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + songId.hashCode()
        result = 31 * result + genreId.hashCode()
        return result
    }
}