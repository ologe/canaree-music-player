package dev.olog.core.entity.track

data class Artist (
        val id: Long,
        val name: String,
        val albumArtist: String,
        val songs: Int,
        val albums: Int,
        val isPodcast: Boolean
)