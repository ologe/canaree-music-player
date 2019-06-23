package dev.olog.core.entity.podcast

data class PodcastArtist (
        val id: Long,
        val name: String,
        val albumArtist: String,
        val songs: Int,
        val albums: Int
)