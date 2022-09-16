package dev.olog.feature.edit.artist

data class DisplayableArtist(
    val id: Long,
    val title: String,
    val songs: Int,
    val isPodcast: Boolean
)