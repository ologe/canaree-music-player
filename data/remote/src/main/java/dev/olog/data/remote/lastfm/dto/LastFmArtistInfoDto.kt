package dev.olog.data.remote.lastfm.dto

import dev.olog.domain.entity.LastFmArtist

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