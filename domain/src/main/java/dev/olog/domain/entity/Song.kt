package dev.olog.domain.entity

data class Song (
        val id: Long,
        val artistId: Long,
        val albumId: Long,
        val title: String,
        val artist: String,
        val album: String,
        val image: String,
        val duration: Long,
        val dateAdded: Long,
        val isRemix: Boolean,
        val isExplicit: Boolean,
        val path: String,
        val folder: String
)