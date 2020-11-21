package dev.olog.data.api.lastfm.artist

import dev.olog.core.entity.LastFmArtist
import dev.olog.data.remote.lastfm.dto.LastFmBioDto
import dev.olog.data.remote.lastfm.dto.LastFmImageDto
import dev.olog.data.remote.lastfm.dto.LastFmArtistInfoDto
import dev.olog.data.remote.lastfm.dto.LastFmArtistInfoResultDto
import dev.olog.data.remote.lastfm.dto.toDomain
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.Test

class LastFmArtistInfoDtoTest {

    @Test
    fun `test mapper, should return null when artist is null`() {
        val model = LastFmArtistInfoDto(
            artist = null
        )

        // when
        val actual = model.toDomain(1)

        // then
        assertEquals(null, actual)
    }

    @Test
    fun `test mapper, should map with blank image when artist is not null`() {
        val artistId = 1L

        val model = LastFmArtistInfoDto(
            artist = LastFmArtistInfoResultDto.EMPTY.copy(
                bio = LastFmBioDto.EMPTY.copy(
                    content = "wiki"
                ),
                mbid = "mbid",
                image = listOf(
                    LastFmImageDto.EMPTY.copy(text = "image")
                )
            )
        )

        // when
        val actual = model.toDomain(artistId)

        // then
        val expected = LastFmArtist(
            id = artistId,
            image = "",
            mbid = "mbid",
            wiki = "wiki"
        )
        assertEquals(expected, actual)
    }

}