package dev.olog.msc.domain.entity

data class UneditedSong(
        val id: Long,
        val artistId: Long,
        val albumId: Long,
        val title: String,
        val artist: String,
        val album: String,
        val path: String,
        val trackNumber: Int,
        val year: Int,
        val image: String,
        val duration: Long,
        val size: Long
)