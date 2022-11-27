package dev.olog.data.api.lastfm.track

import dev.olog.data.api.lastfm.LastFmImageDto

data class LastFmTrackSearchDto(
    val results: Results? = null
) {

    data class Results(
        val trackmatches: Trackmatches? = null,
    )

    data class Trackmatches(
        val track: List<Track> = emptyList(),
    )

    data class Track(
        val name: String? = null,
        val mbid: String? = null,
        val artist: String? = null,
        val url: String? = null,
        val image: List<LastFmImageDto> = emptyList(),
    )

    private val match = this.results?.trackmatches?.track?.firstOrNull()
    val title = match?.name
    val artist = match?.artist

}