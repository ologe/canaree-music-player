package dev.olog.lib.audio.tagger

data class Tags(
    val title: String?,
    val artist: String?,
    val albumArtist: String?,
    val album: String?,
    val genre: String?,
    val year: String?,
    val discNo: String?,
    val trackNo: String?,
    val bpm: String?,
    val bitrate: String,
    val format: String,
    val sampling: String
)