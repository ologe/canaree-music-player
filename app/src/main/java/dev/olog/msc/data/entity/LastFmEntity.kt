package dev.olog.msc.data.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

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

@Entity(tableName = "last_fm_podcast",
        indices = [(Index("id"))])
data class LastFmPodcastEntity(
        @PrimaryKey val id: Long,
        val title: String,
        val artist: String,
        val album: String,
        val image: String,
        val added: String
)

@Entity(tableName = "last_fm_podcast_album",
        indices = [(Index("id"))])
data class LastFmPodcastAlbumEntity(
        @PrimaryKey val id: Long,
        val title: String,
        val artist: String,
        val image: String,
        val added: String
)

@Entity(tableName = "last_fm_podcast_artist",
        indices = [(Index("id"))])
data class LastFmPodcastArtistEntity(
        @PrimaryKey val id: Long,
        val image: String,
        val added: String
)