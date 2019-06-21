package dev.olog.core.entity

data class PodcastAlbum (
        val id: Long,
        val artistId: Long,
        val title: String,
        val artist: String,
        val albumArtist: String,
        val songs: Int,
        val hasSameNameAsFolder: Boolean
)