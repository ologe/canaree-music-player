package dev.olog.core.entity.track

data class Album (
        val id: Long,
        val artistId: Long,
        val title: String,
        val artist: String,
        val albumArtist: String,
        val songs: Int,
        val hasSameNameAsFolder: Boolean,
        val isPodcast: Boolean
)