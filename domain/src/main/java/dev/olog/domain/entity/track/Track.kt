package dev.olog.domain.entity.track

import dev.olog.domain.mediaid.MediaId
import dev.olog.domain.mediaid.MediaIdCategory
import java.io.File

sealed class Track(
    open val id: Long,
    open val artistId: Long,
    open val albumId: Long,
    open val title: String,
    open val artist: String,
    open val albumArtist: String,
    open val album: String,
    open val duration: Long,
    open val dateAdded: Long,
    open val dateModified: Long,
    open val path: String,
    open val trackColumn: Int,
    open val isPodcast: Boolean,
) {

    companion object;

    data class Song(
        override val id: Long,
        override val artistId: Long,
        override val albumId: Long,
        override val title: String,
        override val artist: String,
        override val albumArtist: String,
        override val album: String,
        override val duration: Long,
        override val dateAdded: Long,
        override val dateModified: Long,
        override val path: String,
        override val trackColumn: Int,
        override val isPodcast: Boolean,
    ): Track(
        id = id,
        artistId = artistId,
        albumId = albumId,
        title = title,
        artist = artist,
        albumArtist = albumArtist,
        album = album,
        duration = duration,
        dateAdded = dateAdded,
        dateModified = dateModified,
        path = path,
        trackColumn = trackColumn,
        isPodcast = isPodcast
    )

    data class PlaylistSong(
        override val id: Long,
        override val artistId: Long,
        override val albumId: Long,
        override val title: String,
        override val artist: String,
        override val albumArtist: String,
        override val album: String,
        override val duration: Long,
        override val dateAdded: Long,
        override val dateModified: Long,
        override val path: String,
        override val trackColumn: Int,
        override val isPodcast: Boolean,
        val playlistId: Long,
        val idInPlaylist: Long,
    ): Track(
        id = id,
        artistId = artistId,
        albumId = albumId,
        title = title,
        artist = artist,
        albumArtist = albumArtist,
        album = album,
        duration = duration,
        dateAdded = dateAdded,
        dateModified = dateModified,
        path = path,
        trackColumn = trackColumn,
        isPodcast = isPodcast
    ) {
        companion object
    }

    val hasUnknownArtist: Boolean
        get() = this.artist == "<unknown>"
    val hasUnknownAlbum: Boolean
        get() = this.album == "<unknown>"

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

    val folderPath: String
        get() = path.substring(0, path.lastIndexOf(File.separator))

    fun getMediaId(): MediaId {
        if (this is PlaylistSong) {
            val category = if (isPodcast) MediaIdCategory.PODCASTS_PLAYLIST else MediaIdCategory.PLAYLISTS
            val mediaId = MediaId.createCategoryValue(category, playlistId.toString())
            return MediaId.playableItem(mediaId, id)
        }
        val category = if (isPodcast) MediaIdCategory.PODCASTS else MediaIdCategory.SONGS
        val mediaId = MediaId.createCategoryValue(category, "all")
        return MediaId.playableItem(mediaId, id)
    }

    fun getAlbumMediaId(): MediaId {
        val category = if (isPodcast) MediaIdCategory.PODCASTS_ALBUMS else MediaIdCategory.ALBUMS
        return MediaId.createCategoryValue(category, this.albumId.toString())
    }

    fun getArtistMediaId(): MediaId {
        val category = if (isPodcast) MediaIdCategory.PODCASTS_ARTISTS else MediaIdCategory.ARTISTS
        return MediaId.createCategoryValue(category, this.artistId.toString())
    }

}

fun Track.toPlaylistSong(
    playlistId: Long,
    idInPlaylist: Long,
): Track.PlaylistSong {
    return Track.PlaylistSong(
        id = id,
        artistId = artistId,
        albumId = albumId,
        title = title,
        artist = artist,
        albumArtist = albumArtist,
        album = album,
        duration = duration,
        dateAdded = dateAdded,
        dateModified = dateModified,
        path = path,
        trackColumn = trackColumn,
        isPodcast = isPodcast,
        playlistId = playlistId,
        idInPlaylist = idInPlaylist
    )
}

val Track.Companion.EMPTY: Track.Song
    get() = Track.Song(
        id = 0,
        artistId = 0,
        albumId = 0,
        title = "",
        artist = "",
        albumArtist = "",
        album = "",
        duration = 0,
        dateAdded = 0,
        dateModified = 0,
        path = "",
        trackColumn = 0,
        isPodcast = false,
    )

val Track.PlaylistSong.Companion.EMPTY: Track.PlaylistSong
    get() = Track.PlaylistSong(
        id = 0,
        artistId = 0,
        albumId = 0,
        title = "",
        artist = "",
        albumArtist = "",
        album = "",
        duration = 0,
        dateAdded = 0,
        dateModified = 0,
        path = "",
        trackColumn = 0,
        isPodcast = false,
        playlistId = 0,
        idInPlaylist = 0,
    )