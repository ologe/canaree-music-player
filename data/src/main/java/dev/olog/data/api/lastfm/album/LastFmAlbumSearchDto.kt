package dev.olog.data.api.lastfm.album

import dev.olog.data.api.lastfm.LastFmImageDto
import me.xdrop.fuzzywuzzy.FuzzySearch

data class LastFmAlbumSearchDto(
    val results: Results? = null
) {

    data class Results(
        val albummatches: Albummatches? = null
    )

    class Albummatches {
        val album: List<Album> = emptyList()
    }

    data class Album(
        val name: String? = null,
        val artist: String? = null,
        val url: String? = null,
        val image: List<LastFmImageDto> = emptyList(),
        val mbid: String? = null,
    )

    fun findBestAlbum(originalArtist: String): Album? {
        val results = this.results?.albummatches
            ?.album.orEmpty().takeIf { it.isNotEmpty() }
            ?: return null

        val bestArtist = FuzzySearch.extractOne(originalArtist, results.map { it.artist }).string
        return results.firstOrNull { it.artist == bestArtist } ?: return null
    }

}