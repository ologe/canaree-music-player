package dev.olog.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "playlist")
class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    @JvmField
    val id: Long = 0,
    @JvmField
    val name: String,
    @JvmField
    val size: Int
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlaylistEntity

        if (id != other.id) return false
        if (name != other.name) return false
        if (size != other.size) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + size
        return result
    }
}

@Entity(
    tableName = "playlist_tracks",
    indices = [Index("playlistId")],
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["id"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
class PlaylistTrackEntity(
    @PrimaryKey(autoGenerate = true)
    @JvmField
    val id: Long = 0, // progressive
    @JvmField
    val idInPlaylist: Long,
    @JvmField
    val trackId: Long,
    @JvmField
    val playlistId: Long
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlaylistTrackEntity

        if (id != other.id) return false
        if (idInPlaylist != other.idInPlaylist) return false
        if (trackId != other.trackId) return false
        if (playlistId != other.playlistId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + idInPlaylist.hashCode()
        result = 31 * result + trackId.hashCode()
        result = 31 * result + playlistId.hashCode()
        return result
    }
}