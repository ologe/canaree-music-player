package dev.olog.data.spotify.entity

data class SpotifyArtistAlbumItems(
    val items: List<SpotifyArtistAlbum>
)

data class SpotifyArtistAlbum(
    val album_group: String,
    val album_type: String,
    val id: String,
    val images: List<SpotifyImage>,
    val name: String,
    val release_date: String,
    val release_date_precision: String,
    val total_tracks: Int,
    val type: String,
    val uri: String
)
