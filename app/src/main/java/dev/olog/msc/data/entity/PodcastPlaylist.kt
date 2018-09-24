package dev.olog.msc.data.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "podcast_playlist")
data class PodcastPlaylist(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        val name: String,
        val size: Int
)

@Entity(tableName = "podcast_playlist_tracks",
        indices = [Index("playlistId")],
        foreignKeys = [ForeignKey(
        entity = PodcastPlaylist::class,
        parentColumns = ["id"],
        childColumns = ["playlistId"],
        onDelete = ForeignKey.CASCADE
)])
data class PodcastPlaylistTrack(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        val idInPlaylist: Long,
        val playlistId: Long
)