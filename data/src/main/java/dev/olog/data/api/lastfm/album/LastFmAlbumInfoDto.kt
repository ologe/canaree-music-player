package dev.olog.data.api.lastfm.album

import dev.olog.data.api.lastfm.LastFmImageDto
import dev.olog.data.api.lastfm.LastFmWikiDto

data class LastFmAlbumInfoDto(
    val album: Album? = null
) {

    data class Album(
        val name: String? = null,
        val artist: String? = null,
        val mbid: String? = null,
        val url: String? = null,
        val image: List<LastFmImageDto> = emptyList(),
        val wiki: LastFmWikiDto? = null,
    )
}