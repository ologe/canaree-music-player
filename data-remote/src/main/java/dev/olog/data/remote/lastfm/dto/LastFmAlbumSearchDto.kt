package dev.olog.data.remote.lastfm.dto

import dev.olog.core.entity.LastFmAlbum
import me.xdrop.fuzzywuzzy.FuzzySearch

internal data class LastFmAlbumSearchDto(
    val results: LastFmAlbumSearchResultDto?
) {

    companion object {

        val EMPTY: LastFmAlbumSearchDto
            get() = LastFmAlbumSearchDto(
                results = null
            )

    }

}

internal data class LastFmAlbumSearchResultDto(
    val albummatches: LastFmAlbumSearchMatchesDto?,
) {

    companion object {

        val EMPTY: LastFmAlbumSearchResultDto
            get() = LastFmAlbumSearchResultDto(
                albummatches = null
            )

    }

}

internal data class LastFmAlbumSearchMatchesDto(
    val album: List<LastFmAlbumDto>?
) {

    companion object {

        val EMPTY: LastFmAlbumSearchMatchesDto
            get() = LastFmAlbumSearchMatchesDto(
                album = null
            )

    }

}

internal data class LastFmAlbumDto(
    val artist: String?,
    val image: List<LastFmImageDto>?,
    val mbid: String?,
    val name: String?,
    val streamable: String?,
    val url: String?
) {

    companion object {

        internal val EMPTY: LastFmAlbumDto
            get() = LastFmAlbumDto(
                artist = null,
                image = null,
                mbid = null,
                name = null,
                streamable = null,
                url = null,
            )


    }

}

internal fun LastFmAlbumSearchDto.toDomain(id: Long, originalArtist: String): LastFmAlbum? {
    val results = this.results?.albummatches?.album ?: return null
    val bestArtist = FuzzySearch.extractOne(originalArtist, results.map { it.artist }).string
    val best = results.find { it.artist == bestArtist } ?: return null

    return LastFmAlbum(
        id = id,
        title = best.name.orEmpty(),
        artist = best.artist.orEmpty(),
        image = "",
        mbid = "",
        wiki = ""
    )
}