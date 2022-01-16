package dev.olog.core.last.fm

import dev.olog.core.MediaUri

data class LastFmAlbum(
    val uri: MediaUri,
    val title: String,
    val artist: String,
    val image: String,
    val mbid: String,
    val wiki: String
)