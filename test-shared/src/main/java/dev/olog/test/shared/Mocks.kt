package dev.olog.test.shared

import dev.olog.domain.entity.track.*

object Mocks {

    val song = Song(
        id = 1,
        artistId = 10,
        albumId = 100,
        title = "song",
        artist = "artist",
        albumArtist = "album artist",
        album = "artist",
        duration = 10000,
        dateAdded = 20000,
        dateModified = 30000,
        path = "/path/song.mp3",
        trackColumn = 1,
        idInPlaylist = -1,
        isPodcast = false,
        displayName = "display name"
    )

    val podcast = song.copy(isPodcast = true)

    val album = Album(
        id = 1,
        artistId = 10,
        title = "album",
        artist = "artist",
        albumArtist = "album artist",
        songs = 10,
        path = "/path/song.mp3"
    )

    val artist = Artist(
        id = 1,
        name = "artist",
        albumArtist = "album artist",
        songs = 5,
        isPodcast = false
    )

    val podcastArtist = artist.copy(isPodcast = true)

    val genre = Genre(
        id = 1,
        name = "genre",
        size = 10
    )

    val folder = Folder(
        title = "folder",
        path = "/storage/emulated/0/folder",
        size = 10
    )

    val playlist = Playlist(
        id = 1,
        title = "playlist", size = 10,
        isPodcast = false
    )

    val podcastPlaylist = playlist.copy(isPodcast = true)

}