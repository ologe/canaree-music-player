package dev.olog.msc.presentation.edit.track

data class DisplayableSong(
        val id: Long,
        val title: String,
        val artist: String,
        val album: String,
        val genre: String,
        val year: String,
        val disc: String,
        val track: String,
        val image: String
)