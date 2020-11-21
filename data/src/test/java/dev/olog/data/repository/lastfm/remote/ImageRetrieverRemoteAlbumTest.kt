package dev.olog.data.repository.lastfm.remote

import dev.olog.core.entity.LastFmAlbum
import dev.olog.core.entity.track.Album
import dev.olog.core.entity.track.EMPTY
import dev.olog.data.remote.deezer.DeezerService
import dev.olog.data.remote.deezer.dto.DeezerAlbumDto
import dev.olog.data.remote.deezer.dto.DeezerAlbumSearchResultDto
import dev.olog.data.remote.lastfm.LastFmService
import dev.olog.data.remote.LastFmNulls
import dev.olog.data.mapper.toDomain
import dev.olog.data.remote.ImageRetrieverRemoteAlbum
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

class ImageRetrieverRemoteAlbumTest : StatelessSutTest() {

    companion object {
        private val LAST_FM_INFO_SUCCESS = LastFmAlbum(
            id = 1L,
            title = "info title",
            artist = "info artist",
            image = "info image",
            mbid = "info mbid",
            wiki = "info wiki"
        )
        private val LAST_FM_SEARCH_SUCCESS = LastFmAlbum(
            id = 1L,
            title = "search title",
            artist = "search artist",
            image = "search image",
            mbid = "search mbid",
            wiki = "search wiki"
        )

        private val DEEZER_SUCCESS = DeezerAlbumSearchResultDto.EMPTY.copy(
            items = listOf(DeezerAlbumDto.EMPTY_WITH_IMAGES)
        )
    }

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val lastFmService = mockk<LastFmService>()
    private val deezerService = mockk<DeezerService>()
    private val sut = ImageRetrieverRemoteAlbum(
        lastFmService = lastFmService,
        deezerService = deezerService
    )

    @Nested
    inner class FetchDeezer {

        @Test
        fun `should fetch by title only when artist is blank`() = coroutineRule {
            val album = Album.EMPTY.copy(
                title = "title",
                artist = ""
            )
            coEvery { deezerService.getAlbum("title") } returns IoResult.just(DEEZER_SUCCESS)

            // when
            val actual = sut.fetchDeezerAlbumImage(album)

            // then
            assertThat(actual).isEqualTo(
                "cover_xl"
            )
        }

        @Test
        fun `should fetch by artist and title`() = coroutineRule {
            val album = Album.EMPTY.copy(
                title = "title",
                artist = "artist"
            )
            coEvery { deezerService.getAlbum("artist - title") } returns IoResult.just(DEEZER_SUCCESS)

            // when
            val actual = sut.fetchDeezerAlbumImage(album)

            // then
            assertThat(actual).isEqualTo(
                "cover_xl"
            )
        }

        @Test
        fun `should return null on server error`() = coroutineRule {
            val album = Album.EMPTY.copy(
                title = "title",
                artist = ""
            )
            coEvery { deezerService.getAlbum("title") } returns IoResult.ServerError(0, "")

            // when
            val actual = sut.fetchDeezerAlbumImage(album)

            // then
            assertThat(actual).isNull()
        }

    }

    @Nested
    inner class FetchLastFm {

        @Test
        fun `should return nullAlbum when title is unknown`() = coroutineRule {
            val album = Album.EMPTY.copy(
                id = 1L,
                title = "<unknown>",
                artist = "artist"
            )
            val actual = sut.fetchLastFmAlbumImage(album)

            assertThat(actual).isEqualTo(
                LastFmNulls.createNullAlbum(1L).toDomain()
            )
        }

        @Test
        fun `should fetch albumInfo only, when has valid title and artist`() = coroutineRule {
            val album = Album.EMPTY.copy(
                id = 1L,
                title = "title",
                artist = "artist"
            )
            coEvery { lastFmService.getAlbumInfo(album) } returns LAST_FM_INFO_SUCCESS

            val actual = sut.fetchLastFmAlbumImage(album)

            assertThat(actual).isEqualTo(
                LastFmAlbum(
                    id = 1,
                    title = "info title",
                    artist = "info artist",
                    image = "info image",
                    mbid = "info mbid",
                    wiki = "info wiki"
                )
            )
        }

        @Test
        fun `should search album (not albuminfo), when unknown artist`() = coroutineRule {
            val album = Album.EMPTY.copy(
                id = 1L,
                title = "title",
                artist = "<unknown>"
            )
            coEvery { lastFmService.searchAlbum(album) } returns LAST_FM_SEARCH_SUCCESS

            val actual = sut.fetchLastFmAlbumImage(album)

            assertThat(actual).isEqualTo(
                LastFmAlbum(
                    id = 1,
                    title = "search title",
                    artist = "search artist",
                    image = "search image",
                    mbid = "search mbid",
                    wiki = "search wiki"
                )
            )
        }

        @Test
        fun `should search album, when album info returns null`() = coroutineRule {
            val album = Album.EMPTY.copy(
                id = 1L,
                title = "title",
                artist = "artist"
            )
            coEvery { lastFmService.getAlbumInfo(album) } returns null
            coEvery { lastFmService.searchAlbum(album) } returns LAST_FM_SEARCH_SUCCESS

            val actual = sut.fetchLastFmAlbumImage(album)

            assertThat(actual).isEqualTo(
                LastFmAlbum(
                    id = 1,
                    title = "search title",
                    artist = "search artist",
                    image = "search image",
                    mbid = "search mbid",
                    wiki = "search wiki"
                )
            )
        }

        @Test
        fun `should return nullAlbum, when both search and album info returns null`() = coroutineRule {
            val album = Album.EMPTY.copy(
                id = 1L,
                title = "title",
                artist = "artist"
            )
            coEvery { lastFmService.getAlbumInfo(album) } returns null
            coEvery { lastFmService.searchAlbum(album) } returns null

            val actual = sut.fetchLastFmAlbumImage(album)

            assertThat(actual).isEqualTo(
                LastFmNulls.createNullAlbum(1L).toDomain()
            )
        }

    }

    @Nested
    inner class Fetch {

        @Test
        fun `should return model with deezer image when deezer model is non null`() = coroutineRule {
            val album = Album.EMPTY.copy(
                id = 1L,
                title = "title",
                artist = "artist"
            )
            coEvery { deezerService.getAlbum("artist - title") } returns IoResult.just(DEEZER_SUCCESS)
            coEvery { lastFmService.getAlbumInfo(album) } returns LAST_FM_INFO_SUCCESS

            val actual = sut.fetch(album)

            assertThat(actual).isEqualTo(
                LastFmAlbum(
                    1L,
                    title = "info title",
                    artist = "info artist",
                    image = "cover_xl",
                    mbid = "info mbid",
                    wiki = "info wiki"
                )
            )
        }

        @Test
        fun `should return model with last fm image when deezer model is null`() = coroutineRule {
            val album = Album.EMPTY.copy(
                id = 1L,
                title = "title",
                artist = "artist"
            )
            coEvery { deezerService.getAlbum("artist - title") } returns IoResult.ServerError(0, "")
            coEvery { lastFmService.getAlbumInfo(album) } returns LAST_FM_INFO_SUCCESS

            val actual = sut.fetch(album)

            assertThat(actual).isEqualTo(
                LastFmAlbum(
                    1L,
                    title = "info title",
                    artist = "info artist",
                    image = "info image",
                    mbid = "info mbid",
                    wiki = "info wiki"
                )
            )
        }

    }

}