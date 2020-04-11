package dev.olog.lib.audio.tagger

data class Tags(
    val title: String? = null,
    val artist: String? = null,
    val albumArtist: String? = null,
    val album: String? = null,
    val genre: String? = null,
    val year: String? = null,
    val discNo: String? = null,
    val trackNo: String? = null,
    val bpm: String? = null,
    val bitrate: String = "",
    val format: String = "",
    val sampling: String = ""
)