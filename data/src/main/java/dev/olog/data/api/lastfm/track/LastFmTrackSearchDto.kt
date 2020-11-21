package dev.olog.data.api.lastfm.track

import dev.olog.core.entity.LastFmTrack
import dev.olog.data.api.lastfm.entity.LastFmImageDto

internal data class LastFmTrackSearchDto(
    val results: LastFmTrackSearchResultDto?
) {

    companion object {

        val EMPTY: LastFmTrackSearchDto
            get() = LastFmTrackSearchDto(
                results = null,
            )

    }

}

internal data class LastFmTrackSearchResultDto(
    val trackmatches: LastFmTrackSearchMatchesDto?
) {

    companion object {

        val EMPTY: LastFmTrackSearchResultDto
            get() = LastFmTrackSearchResultDto(
                trackmatches = null,
            )

    }

}

internal data class LastFmTrackSearchMatchesDto(
    val track: List<LastFmTrackDto>?
) {

    companion object {

        val EMPTY: LastFmTrackSearchMatchesDto
            get() = LastFmTrackSearchMatchesDto(
                track = null
            )

    }

}

internal data class LastFmTrackDto(
    val artist: String?,
    val image: List<LastFmImageDto>?,
    val mbid: String?,
    val name: String?,
) {

    companion object {

        val EMPTY: LastFmTrackDto
            get() = LastFmTrackDto(
                artist = null,
                image = null,
                mbid = null,
                name = null,
            )

    }

}

internal fun LastFmTrackSearchDto.toDomain(id: Long): LastFmTrack? {
    // TODO use fuzzy for best match?
    val track = this.results?.trackmatches?.track?.firstOrNull() ?: return null

    return LastFmTrack(
        id = id,
        title = track.name.orEmpty(),
        artist = track.artist.orEmpty(),
        album = "",
        image = "",
        mbid = "",
        artistMbid = "",
        albumMbid = ""
    )
}