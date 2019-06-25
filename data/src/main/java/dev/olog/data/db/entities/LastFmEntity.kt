package dev.olog.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "last_fm_track",
        indices = [(Index("id"))])
data class LastFmTrackEntity(
        @PrimaryKey val id: Long,
        val title: String,
        val artist: String,
        val album: String,
        val image: String,
        val added: String
)

@Entity(tableName = "last_fm_album",
        indices = [(Index("id"))])
data class LastFmAlbumEntity(
        @PrimaryKey val id: Long,
        val title: String,
        val artist: String,
        val image: String,
        val added: String
)

@Entity(tableName = "last_fm_artist",
        indices = [(Index("id"))])
data class LastFmArtistEntity(
        @PrimaryKey val id: Long,
        val image: String,
        val added: String
)