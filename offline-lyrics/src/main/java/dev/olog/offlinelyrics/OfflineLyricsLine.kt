package dev.olog.offlinelyrics

internal data class Lyrics(
    val lines: List<OfflineLyricsLine>
)

internal data class OfflineLyricsLine(
    val value: String,
    val time: Long
)