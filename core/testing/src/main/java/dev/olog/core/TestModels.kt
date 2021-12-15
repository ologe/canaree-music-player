package dev.olog.core

import dev.olog.core.entity.PlayingQueueSong
import dev.olog.core.entity.track.Song

val Song.Companion.EMPTY: Song
    get() = Song(
        id = 0,
        artistId = 0,
        albumId = 0,
        title = "",
        artist = "",
        albumArtist = "",
        album = "",
        duration = 0,
        dateAdded = 0,
        directory = "",
        path = "",
        discNumber = 0,
        trackNumber = 0,
        idInPlaylist = 0,
        isPodcast = false
    )

val PlayingQueueSong.Companion.EMPTY: PlayingQueueSong
    get() = PlayingQueueSong(
        song = Song.EMPTY,
        playOrder = 0,
    )