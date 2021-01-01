package dev.olog.data.repository.lastfm.remote

import dev.olog.domain.entity.LastFmTrack
import dev.olog.domain.entity.track.EMPTY
import dev.olog.domain.entity.track.Track
import dev.olog.data.remote.deezer.DeezerService
import dev.olog.data.remote.deezer.dto.DeezerAlbumDto
import dev.olog.data.remote.deezer.dto.DeezerTrackDto
import dev.olog.data.remote.deezer.dto.DeezerTrackSearchResultDto
import dev.olog.data.remote.lastfm.LastFmService
import dev.olog.data.remote.LastFmNulls
import dev.olog.data.mapper.toDomain
import dev.olog.data.remote.ImageRetrieverRemoteTrack
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

class ImageRetrieverRemoteTrackTest : StatelessSutTest() {

    companion object {
        private val LAST_FM_INFO_SUCCESS = LastFmTrack(
            id = 1L,
            title = "info title",
            artist = "info artist",
            album = "info album",
            image = "info image",
            mbid = "info mbid",
            albumMbid = "info albumMbid",
            artistMbid = "info artistMbid",
        )
        private val LAST_FM_SEARCH_SUCCESS = LastFmTrack(
            id = 1L,
            title = "search title",
            artist = "search artist",
            album = "search album",
            image = "search image",
            mbid = "search mbid",
            albumMbid = "search albumMbid",
            artistMbid = "search artistMbid",
        )

        private val DEEZER_SUCCESS = DeezerTrackSearchResultDto.EMPTY.copy(
            items = listOf(
                DeezerTrackDto.EMPTY.copy(
                    album = DeezerAlbumDto.EMPTY_WITH_IMAGES
                )
            )
        )
    }

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val lastFmService = mockk<LastFmService>()
    private val deezerService = mockk<DeezerService>()
    private val sut = ImageRetrieverRemoteTrack(
        lastFmService = lastFmService,
        deezerService = deezerService
    )

    @Nested
    inner class FetchDeezer {

        @Test
        fun `should fetch by title only when artist is blank`() = coroutineRule {
            coEvery { deezerService.getTrack("title") } returns IoResult.just(DEEZER_SUCCESS)

            // when
            val actual = sut.fetchDeezerTrackImage("title", "")

            // then
            assertThat(actual).isEqualTo(
                "cover_xl"
            )
        }

        @Test
        fun `should fetch by artist and title`() = coroutineRule {
            coEvery { deezerService.getTrack("title") } returns IoResult.just(DEEZER_SUCCESS)

            // when
            val actual = sut.fetchDeezerTrackImage("title", "")

            // then
            assertThat(actual).isEqualTo(
                "cover_xl"
            )
        }

        @Test
        fun `should return null on server error`() = coroutineRule {
            coEvery { deezerService.getTrack("title") } returns IoResult.ServerError(0, "")

            // when
            val actual = sut.fetchDeezerTrackImage("title", "")

            // then
            assertThat(actual).isNull()
        }

    }

    @Nested
    inner class FetchLastFm {

        @Test
        fun `should fetch trackInfo only, when has valid title and artist`() = coroutineRule {
            val track = Track.EMPTY.copy(
                id = 1L,
                title = "title",
                artist = "artist"
            )
            coEvery { lastFmService.getTrackInfo(1L, "title", "artist") } returns LAST_FM_INFO_SUCCESS

            val actual = sut.fetchLastFmTrack(track, "title", "artist")

            assertThat(actual).isEqualTo(
                LastFmTrack(
                    id = 1,
                    title = "info title",
                    artist = "info artist",
                    image = "info image",
                    mbid = "info mbid",
                    album = "info album",
                    artistMbid = "info artistMbid",
                    albumMbid = "info albumMbid",
                )
            )
        }

        @Test
        fun `should search track (no track info), when unknown artist`() = coroutineRule {
            val track = Track.EMPTY.copy(
                id = 1L,
                title = "title",
                artist = "<unknown>"
            )
            coEvery { lastFmService.searchTrack(1L, "title", "<unknown>") } returns LAST_FM_SEARCH_SUCCESS

            val actual = sut.fetchLastFmTrack(track, "title", "<unknown>")

            assertThat(actual).isEqualTo(
                LastFmTrack(
                    id = 1,
                    title = "search title",
                    artist = "search artist",
                    image = "search image",
                    mbid = "search mbid",
                    album = "search album",
                    artistMbid = "search artistMbid",
                    albumMbid = "search albumMbid",
                )
            )
        }

        @Test
        fun `should search track, when track info returns null`() = coroutineRule {
            val track = Track.EMPTY.copy(
                id = 1L,
                title = "title",
                artist = "artist"
            )
            coEvery { lastFmService.getTrackInfo(1L, "title", "artist") } returns null
            coEvery { lastFmService.searchTrack(1L, "title", "artist") } returns LAST_FM_SEARCH_SUCCESS

            val actual = sut.fetchLastFmTrack(track, "title", "artist")

            assertThat(actual).isEqualTo(
                LastFmTrack(
                    id = 1,
                    title = "search title",
                    artist = "search artist",
                    image = "search image",
                    mbid = "search mbid",
                    album = "search album",
                    artistMbid = "search artistMbid",
                    albumMbid = "search albumMbid",
                )
            )
        }

        @Test
        fun `should return nullTrack, when both search and track info returns null`() = coroutineRule {
            val track = Track.EMPTY.copy(
                id = 1L,
                title = "title",
                artist = "artist"
            )
            coEvery { lastFmService.getTrackInfo(1L, "title", "artist") } returns null
            coEvery { lastFmService.searchTrack(1L, "title", "artist") } returns null

            val actual = sut.fetchLastFmTrack(track, "title", "artist")

            assertThat(actual).isEqualTo(
                LastFmNulls.createNullTrack(1L).toDomain()
            )
        }

    }

    @Nested
    inner class Fetch {

        @Test
        fun `should return model with deezer image when deezer model is non null`() = coroutineRule {
            val track = Track.EMPTY.copy(
                id = 1L,
                title = "title",
                artist = "artist"
            )
            coEvery { deezerService.getTrack("title - artist") } returns IoResult.just(DEEZER_SUCCESS)
            coEvery { lastFmService.getTrackInfo(1L, "title", "artist") } returns LAST_FM_INFO_SUCCESS

            val actual = sut.fetch(track)

            assertThat(actual).isEqualTo(
                LastFmTrack(
                    1L,
                    title = "info title",
                    artist = "info artist",
                    image = "cover_xl",
                    mbid = "info mbid",
                    album = "info album",
                    albumMbid = "info albumMbid",
                    artistMbid = "info artistMbid"
                )
            )
        }

        @Test
        fun `should return model with last fm image when deezer model is null`() = coroutineRule {
            val track = Track.EMPTY.copy(
                id = 1L,
                title = "title",
                artist = "artist"
            )
            coEvery { deezerService.getTrack("title - artist") } returns IoResult.ServerError(0, "")
            coEvery { lastFmService.getTrackInfo(1L, "title", "artist") } returns LAST_FM_INFO_SUCCESS

            val actual = sut.fetch(track)

            assertThat(actual).isEqualTo(
                LastFmTrack(
                    1L,
                    title = "info title",
                    artist = "info artist",
                    image = "info image",
                    mbid = "info mbid",
                    album = "info album",
                    albumMbid = "info albumMbid",
                    artistMbid = "info artistMbid"
                )
            )
        }

    }

}