package dev.olog.data.local.playlist

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.olog.domain.entity.track.Playlist

@Entity(tableName = "playlist")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val size: Int
)

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
data class PlaylistTrackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // progressive
    val idInPlaylist: Long,
    val trackId: Long,
    val playlistId: Long
)

fun PlaylistEntity.toDomain(): Playlist {
    return Playlist(
        id = this.id,
        title = this.name,
        size = this.size,
        isPodcast = false
    )
}