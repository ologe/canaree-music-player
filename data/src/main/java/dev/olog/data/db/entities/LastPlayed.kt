package dev.olog.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "last_played_artists",
    indices = [(Index("id"))]
)
class LastPlayedArtistEntity(
    @JvmField
    @PrimaryKey
    val id: Long,
    @JvmField
    val dateAdded: Long = System.currentTimeMillis()
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LastPlayedArtistEntity

        if (id != other.id) return false
        if (dateAdded != other.dateAdded) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + dateAdded.hashCode()
        return result
    }
}

@Entity(
    tableName = "last_played_albums",
    indices = [(Index("id"))]
)
class LastPlayedAlbumEntity(
    @JvmField
    @PrimaryKey
    val id: Long,
    @JvmField
    val dateAdded: Long = System.currentTimeMillis()
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LastPlayedAlbumEntity

        if (id != other.id) return false
        if (dateAdded != other.dateAdded) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + dateAdded.hashCode()
        return result
    }
}

@Entity(
    tableName = "last_played_podcast_albums",
    indices = [(Index("id"))]
)
class LastPlayedPodcastAlbumEntity(
    @JvmField
    @PrimaryKey
    val id: Long,
    @JvmField
    val dateAdded: Long = System.currentTimeMillis()
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LastPlayedPodcastAlbumEntity

        if (id != other.id) return false
        if (dateAdded != other.dateAdded) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + dateAdded.hashCode()
        return result
    }
}

@Entity(
    tableName = "last_played_podcast_artists",
    indices = [(Index("id"))]
)
class LastPlayedPodcastArtistEntity(
    @JvmField
    @PrimaryKey
    val id: Long,
    @JvmField
    val dateAdded: Long = System.currentTimeMillis()
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LastPlayedPodcastArtistEntity

        if (id != other.id) return false
        if (dateAdded != other.dateAdded) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + dateAdded.hashCode()
        return result
    }
}