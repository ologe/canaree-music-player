package dev.olog.core.last.fm

import dev.olog.core.MediaUri

data class LastFmArtist(
    val uri: MediaUri,
    val image: String,
    val mbid: String,
    val wiki: String
)