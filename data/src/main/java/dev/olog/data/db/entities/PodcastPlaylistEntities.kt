package dev.olog.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "podcast_playlist")
class PodcastPlaylistEntity(
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

        other as PodcastPlaylistEntity

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
    tableName = "podcast_playlist_tracks",
    indices = [Index("playlistId")],
    foreignKeys = [
        ForeignKey(
            entity = PodcastPlaylistEntity::class,
            parentColumns = ["id"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PodcastPlaylistTrackEntity(
    @PrimaryKey(autoGenerate = true)
    @JvmField
    val id: Long = 0, // progressive
    @JvmField
    val idInPlaylist: Long,
    @JvmField
    val podcastId: Long,
    @JvmField
    val playlistId: Long
)