package dev.olog.data.api.lastfm.artist

import dev.olog.core.entity.LastFmArtist
import dev.olog.data.api.lastfm.entity.LastFmBioDto
import dev.olog.data.api.lastfm.entity.LastFmImageDto

internal data class LastFmArtistInfoDto(
    val artist: LastFmArtistInfoResultDto?
) {

    companion object {

        val EMPTY: LastFmArtistInfoDto
            get() = LastFmArtistInfoDto(
                artist = null
            )

    }

}

internal data class LastFmArtistInfoResultDto(
    val bio: LastFmBioDto?,
    val image: List<LastFmImageDto>?,
    val mbid: String?,
    val name: String?,
    val url: String?
) {

    companion object {

        internal val EMPTY: LastFmArtistInfoResultDto
            get() = LastFmArtistInfoResultDto(
                bio = null,
                image = null,
                mbid = null,
                name = null,
                url = null
            )


    }

}

// don't get image, only wiki. image is taken from deezer
internal fun LastFmArtistInfoDto.toDomain(id: Long): LastFmArtist? {
    val artist = this.artist ?: return null
    return LastFmArtist(
        id = id,
        image = "",
        mbid = artist.mbid.orEmpty(),
        wiki = artist.bio?.content.orEmpty()
    )
}