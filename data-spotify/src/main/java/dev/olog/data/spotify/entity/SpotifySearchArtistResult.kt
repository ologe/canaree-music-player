package dev.olog.data.spotify.entity

data class SpotifySearchArtistResult(
    val artists: SpotifySearchArtistList
)

data class SpotifySearchArtistList(
    val items: List<SpotifySearchArtist>
)

data class SpotifySearchArtist(
    val genres: List<Any>,
    val href: String,
    val id: String,
    val images: List<SpotifyImage>,
    val name: String,
    val popularity: Int,
    val type: String,
    val uri: String
)

