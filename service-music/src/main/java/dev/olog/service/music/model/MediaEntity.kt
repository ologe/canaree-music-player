package dev.olog.service.music.model

import dev.olog.core.MediaId
import dev.olog.core.entity.PlayingQueueSong
import dev.olog.core.entity.track.Song

internal class MediaEntity(
    @JvmField
    val id: Long,
    @JvmField
    val idInPlaylist: Int,
    @JvmField
    val mediaId: MediaId,
    @JvmField
    val artistId: Long,
    @JvmField
    val albumId: Long,
    @JvmField
    val title: String,
    @JvmField
    val artist: String,
    @JvmField
    val albumArtist: String,
    @JvmField
    val album: String,
    @JvmField
    val duration: Long,
    @JvmField
    val dateAdded: Long,
    @JvmField
    val path: String,
    @JvmField
    val discNumber: Int,
    @JvmField
    val trackNumber: Int,
    @JvmField
    val isPodcast: Boolean
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MediaEntity

        if (id != other.id) return false
        if (idInPlaylist != other.idInPlaylist) return false
        if (mediaId != other.mediaId) return false
        if (artistId != other.artistId) return false
        if (albumId != other.albumId) return false
        if (title != other.title) return false
        if (artist != other.artist) return false
        if (albumArtist != other.albumArtist) return false
        if (album != other.album) return false
        if (duration != other.duration) return false
        if (dateAdded != other.dateAdded) return false
        if (path != other.path) return false
        if (discNumber != other.discNumber) return false
        if (trackNumber != other.trackNumber) return false
        if (isPodcast != other.isPodcast) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + idInPlaylist
        result = 31 * result + mediaId.hashCode()
        result = 31 * result + artistId.hashCode()
        result = 31 * result + albumId.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + albumArtist.hashCode()
        result = 31 * result + album.hashCode()
        result = 31 * result + duration.hashCode()
        result = 31 * result + dateAdded.hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + discNumber
        result = 31 * result + trackNumber
        result = 31 * result + isPodcast.hashCode()
        return result
    }

    fun withIdInPlaylist(idInPlaylist: Int): MediaEntity {
        return MediaEntity(
            id = id,
            idInPlaylist = idInPlaylist,
            mediaId = mediaId,
            artistId = artistId,
            albumId = albumId,
            title = title,
            artist = artist,
            albumArtist = albumArtist,
            album = album,
            duration = duration,
            dateAdded = dateAdded,
            path = path,
            discNumber = discNumber,
            trackNumber = trackNumber,
            isPodcast = isPodcast
        )
    }

}

internal fun Song.toMediaEntity(progressive: Int, mediaId: MediaId) : MediaEntity {
    return MediaEntity(
        this.id,
        progressive,
        MediaId.playableItem(mediaId, this.id),
        this.artistId,
        this.albumId,
        this.title,
        this.artist,
        this.albumArtist,
        this.album,
        this.duration,
        this.dateAdded,
        this.path,
        this.discNumber,
        this.trackNumber,
        this.isPodcast
    )
}

internal fun PlayingQueueSong.toMediaEntity() : MediaEntity {
    val song = this.song
    return MediaEntity(
        song.id,
        song.idInPlaylist,
        this.mediaId,
        song.artistId,
        song.albumId,
        song.title,
        song.artist,
        song.albumArtist,
        song.album,
        song.duration,
        song.dateAdded,
        song.path,
        song.discNumber,
        song.trackNumber,
        song.isPodcast
    )
}

internal fun MediaEntity.toPlayerMediaEntity(positionInQueue: PositionInQueue, bookmark: Long) : PlayerMediaEntity {
    return PlayerMediaEntity(this, positionInQueue, bookmark)
}