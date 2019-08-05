package dev.olog.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorite_songs",
    indices = [(Index("songId"))]
)
class FavoriteEntity(
    @PrimaryKey
    @JvmField
    val songId: Long
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FavoriteEntity

        if (songId != other.songId) return false

        return true
    }

    override fun hashCode(): Int {
        return songId.hashCode()
    }
}

@Entity(
    tableName = "favorite_podcast_songs",
    indices = [(Index("podcastId"))]
)
class FavoritePodcastEntity(
    @PrimaryKey
    @JvmField
    val podcastId: Long
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FavoritePodcastEntity

        if (podcastId != other.podcastId) return false

        return true
    }

    override fun hashCode(): Int {
        return podcastId.hashCode()
    }
}
