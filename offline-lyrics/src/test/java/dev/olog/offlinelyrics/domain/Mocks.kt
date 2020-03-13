package dev.olog.offlinelyrics.domain

import dev.olog.core.entity.track.Song

internal object Mocks {

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

}