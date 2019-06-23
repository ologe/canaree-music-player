package dev.olog.core.entity.track

data class Playlist(
    val id: Long,
    val title: String,
    val size: Int,
    val isPodcast: Boolean
)