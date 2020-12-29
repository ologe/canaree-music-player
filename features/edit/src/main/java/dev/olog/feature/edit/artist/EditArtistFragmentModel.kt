package dev.olog.feature.edit.artist

internal data class EditArtistFragmentModel(
    val id: Long,
    val title: String,
    val albumArtist: String,
    val songs: Int,
    val isPodcast: Boolean
)