package dev.olog.data.api.lastfm.track

import dev.olog.data.api.lastfm.LastFmImageDto
import dev.olog.data.api.lastfm.LastFmWikiDto

data class LastFmTrackInfoDto(
    val track: Track? = null,
) {

    data class Track(
        val name: String? = null,
        val mbid: String? = null,
        val listeners: String? = null,
        val artist: Artist? = null,
        val album: Album? = null,
        val wiki: LastFmWikiDto? = null,
    )

    data class Album(
        val artist: String? = null,
        val title: String? = null,
        val mbid: String? = null,
        val url: String? = null,
        val image: List<LastFmImageDto> = emptyList(),
    )

    data class Artist(
        val name: String? = null,
        val mbid: String? = null,
        val url: String? = null,
    )

    val image: String? = track?.album?.image?.reversed()?.find { it.text?.isNotBlank() == true }?.text

}