package dev.olog.lib.offline.lyrics

data class Lyrics(
    val lines: List<OfflineLyricsLine>
)

data class OfflineLyricsLine(
    val value: String,
    val time: Long
)