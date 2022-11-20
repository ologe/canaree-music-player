package dev.olog.data.api.lastfm.artist

import dev.olog.data.api.lastfm.LastFmImageDto

data class LastFmArtistInfoDto(
    val artist: Artist? = null
) {
    data class Artist(
        val name: String? = null,
        val mbid: String? = null,
        val url: String? = null,
        val image: List<LastFmImageDto> = emptyList(),
        val bio: Bio? = null,
    )

    data class Bio(
        val published: String? = null,
        val summary: String? = null,
        val content: String? = null,
    )
}