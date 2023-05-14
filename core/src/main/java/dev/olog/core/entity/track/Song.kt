package dev.olog.core.entity.track

import dev.olog.core.MediaId

data class Song(
    val id: Long,
    val artistId: Long,
    val albumId: Long,
    val title: String,
    val artist: String,
    val albumArtist: String,
    val album: String,
    val duration: Long,
    val dateAdded: Long,
    val path: String,
    val trackColumn: Int,
    @Deprecated(message = "remove")
    val idInPlaylist: Int,
    val isPodcast: Boolean
) {

    val discNumber: Int
        get() {
            if (trackColumn >= 1000) {
                return trackColumn / 1000
            }
            return 0
        }

    val trackNumber: Int
        get() {
            if (trackColumn >= 1000) {
                return trackColumn % 1000
            }
            return trackColumn
        }

    fun getMediaId(): MediaId {
        return MediaId.ofTrack(id, isPodcast)
    }

    fun getAlbumMediaId(): MediaId {
        return MediaId.ofAlbum(albumId, isPodcast)
    }

    fun getArtistMediaId(): MediaId {
        return MediaId.ofArtist(artistId, isPodcast)
    }

}