package dev.olog.feature.edit.author

data class DisplayableArtist(
    val id: Long,
    val title: String,
    val albumArtist: String,
    val songs: Int,
    val isPodcast: Boolean
)