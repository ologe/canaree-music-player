package dev.olog.offlinelyrics

data class Lyrics(
    val lines: List<OfflineLyricsLine>
)

data class OfflineLyricsLine(
    val value: String,
    val time: Long
)