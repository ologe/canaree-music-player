package dev.olog.data.repository.lastfm.remote

import dev.olog.domain.entity.LastFmArtist
import dev.olog.domain.entity.track.Artist
import dev.olog.domain.entity.track.EMPTY
import dev.olog.data.remote.deezer.DeezerService
import dev.olog.data.remote.deezer.dto.DeezerArtistDto
import dev.olog.data.remote.deezer.dto.DeezerArtistSearchResultDto
import dev.olog.data.remote.lastfm.LastFmService
import dev.olog.data.remote.lastfm.dto.LastFmArtistInfoDto
import dev.olog.data.remote.lastfm.dto.LastFmArtistInfoResultDto
import dev.olog.data.remote.lastfm.dto.LastFmBioDto
import dev.olog.data.remote.lastfm.dto.LastFmImageDto
import dev.olog.data.remote.LastFmNulls
import dev.olog.data.mapper.toDomain
import dev.olog.data.remote.ImageRetrieverRemoteArtist
import dev.olog.lib.network.model.IoResult
import dev.olog.lib.network.model.just
import dev.olog.test.shared.MainCoroutineRule
import dev.olog.test.shared.StatelessSutTest
import dev.olog.test.shared.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Rule
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ImageRetrieverRemoteArtistTest : StatelessSutTest() {

    companion object {
        private val LAST_FM_SUCCESS = LastFmArtistInfoDto.EMPTY.copy(
            artist = LastFmArtistInfoResultDto.EMPTY.copy(
                image = listOf(LastFmImageDto.EMPTY.copy(text = "image")),
                bio = LastFmBioDto.EMPTY.copy(content = "wiki"),
                mbid = "mbid"
            )
        )
        private val LAST_FM_INVALID = LastFmArtistInfoDto.EMPTY.copy(
            artist = null
        )

        private val DEEZER_SUCCESS = DeezerArtistSearchResultDto.EMPTY.copy(
            items = listOf(DeezerArtistDto.EMPTY_WITH_IMAGES)
        )
    }

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val lastFmService = mockk<LastFmService>()
    private val deezerService = mockk<DeezerService>()

    private val sut = ImageRetrieverRemoteArtist(
        lastFmService = lastFmService,
        deezerService = deezerService
    )

    @Nested
    inner class FetchDeezerImage {

        @Test
        fun `should return item on success`() = coroutineRule {
            coEvery { deezerService.getArtist("artist") } returns IoResult.just(DEEZER_SUCCESS)

            // when
            val actual = sut.fetchDeezerImage("artist")

            // then
            assertThat(actual).isEqualTo("picture_xl")
        }

        @Test
        fun `should return null on server error`() = coroutineRule {
            coEvery { deezerService.getArtist("artist") } returns IoResult.ServerError(0, "")

            // when
            val actual = sut.fetchDeezerImage("artist")

            // then
            assertThat(actual).isNull()
        }

    }

    @Nested
    inner class FetchLastFm {

        @Test
        fun `should return item on success when LastFmArtistInfoDto#artist is non null`() = coroutineRule {
            val artistId = 1L

            val response = LAST_FM_SUCCESS

            coEvery { lastFmService.getArtistInfo("artist") } returns IoResult.just(response)

            // when
            val actual = sut.fetchLastFm(artistId, "artist")

            // then
            assertThat(actual).isEqualTo(
                LastFmArtist(
                    id = artistId,
                    image = "",
                    mbid = "mbid",
                    wiki = "wiki"
                )
            )
        }

        @Test
        fun `should return null on success when LastFmArtistInfoDto#artist is null`() = coroutineRule {
            val artistId = 1L

            coEvery { lastFmService.getArtistInfo("artist") } returns IoResult.just(LAST_FM_INVALID)

            // when
            val actual = sut.fetchLastFm(artistId, "artist")

            // then
            assertThat(actual).isNull()
        }

        @Test
        fun `should return null on server error`() = coroutineRule {
            coEvery { lastFmService.getArtistInfo("artist") } returns IoResult.ServerError(0, "")

            // when
            val actual = sut.fetchLastFm(1, "artist")

            // then
            assertThat(actual).isNull()
        }

    }

    @Nested
    inner class Fetch {

        @Test
        fun `should return item on success`() = coroutineRule {
            val artist = Artist.EMPTY.copy(
                id = 1L,
                name = "artist name"
            )
            coEvery { lastFmService.getArtistInfo("artist name") } returns IoResult.just(LAST_FM_SUCCESS)
            coEvery { deezerService.getArtist("artist name") } returns IoResult.just(DEEZER_SUCCESS)

            // when
            val actual = sut.fetch(artist)

            // then
            assertThat(actual).isEqualTo(
                LastFmArtist(
                    id = 1L,
                    image = "picture_xl",
                    mbid = "mbid",
                    wiki = "wiki"
                )
            )
        }

        @Test
        fun `should return nullArtist when last fm returns server error`() = coroutineRule {
            val artist = Artist.EMPTY.copy(
                id = 1L,
                name = "artist name"
            )

            coEvery { lastFmService.getArtistInfo("artist name") } returns IoResult.ServerError(0, "")

            coEvery { deezerService.getArtist("artist name") } returns IoResult.just(DEEZER_SUCCESS)

            // when
            val actual = sut.fetch(artist)

            // then
            assertThat(actual).isEqualTo(
                LastFmNulls.createNullArtist(1L).toDomain()
            )
        }

        @Test
        fun `should return nullArtist when deezer returns server error`() = coroutineRule {
            val artist = Artist.EMPTY.copy(
                id = 1L,
                name = "artist name"
            )

            coEvery { lastFmService.getArtistInfo("artist name") } returns IoResult.just(LAST_FM_SUCCESS)

            coEvery { deezerService.getArtist("artist name") } returns IoResult.ServerError(0, "")

            // when
            val actual = sut.fetch(artist)

            // then
            assertThat(actual).isEqualTo(
                LastFmNulls.createNullArtist(1L).toDomain()
            )
        }

        @Test
        fun `should return nullArtist when both last fm and deezer returns server error`() = coroutineRule {
            val artist = Artist.EMPTY.copy(
                id = 1L,
                name = "artist name"
            )

            coEvery { lastFmService.getArtistInfo("artist name") } returns IoResult.ServerError(0, "")

            coEvery { deezerService.getArtist("artist name") } returns IoResult.ServerError(0, "")

            // when
            val actual = sut.fetch(artist)

            // then
            assertThat(actual).isEqualTo(
                LastFmNulls.createNullArtist(1L).toDomain()
            )
        }

    }

}