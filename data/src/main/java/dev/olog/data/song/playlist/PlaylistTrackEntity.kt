package dev.olog.data.song.playlist

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.olog.data.song.playlist.PlaylistEntity

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
@Deprecated("migrate to new playlist")
data class PlaylistTrackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // progressive
    val idInPlaylist: Long,
    val trackId: Long,
    val playlistId: Long
)