package dev.olog.msc.api.last.fm.model

data class SearchedTrack(
        val id: Long,
        val title: String,
        val artist: String,
        val album: String
)