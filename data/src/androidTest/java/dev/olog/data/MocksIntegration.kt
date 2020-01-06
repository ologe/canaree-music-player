package dev.olog.data

import dev.olog.core.entity.track.Song

internal object MocksIntegration {

    val song = Song(
        1,
        10,
        100,
        "song",
        "artist",
        "album",
        "artist",
        10000,
        10000,
        10000,
        "/path/song.mp3",
        1,
        -1,
        false,
        "display name"
    )

    val podcast = song.copy(isPodcast = true)

}