package dev.olog.core.last.fm

import dev.olog.core.MediaUri

data class LastFmTrack(
    val uri: MediaUri,
    val title: String,
    val artist: String,
    val album: String,
    val image: String,
    val mbid: String,
    val artistMbid: String,
    val albumMbid: String
)