package dev.olog.data.repository.lastfm

import dev.olog.core.entity.EMPTY
import dev.olog.core.entity.LastFmAlbum
import dev.olog.core.entity.LastFmArtist
import dev.olog.core.entity.LastFmTrack
import dev.olog.core.entity.track.Album
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.EMPTY
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.track.AlbumGateway
import dev.olog.core.gateway.track.ArtistGateway
import dev.olog.core.gateway.track.SongGateway
import dev.olog.data.repository.lastfm.local.ImageRetrieverLocalAlbum
import dev.olog.data.repository.lastfm.local.ImageRetrieverLocalArtist
import dev.olog.data.repository.lastfm.local.ImageRetrieverLocalTrack
import dev.olog.data.repository.lastfm.remote.ImageRetrieverRemoteAlbum
import dev.olog.data.repository.lastfm.remote.ImageRetrieverRemoteArtist
import dev.olog.data.repository.lastfm.remote.ImageRetrieverRemoteTrack
import dev.olog.test.shared.MainCoroutineRule
import dev.olog.test.shared.StatelessSutTest
import dev.olog.test.shared.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Rule
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ImageRetrieverRepositoryTest : StatelessSutTest() {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private val localTrack = mockk<ImageRetrieverLocalTrack>(relaxUnitFun = true)
    private val remoteTrack = mockk<ImageRetrieverRemoteTrack>()

    private val localArtist = mockk<ImageRetrieverLocalArtist>(relaxUnitFun = true)
    private val remoteArtist = mockk<ImageRetrieverRemoteArtist>()

    private val localAlbum = mockk<ImageRetrieverLocalAlbum>(relaxUnitFun = true)
    private val remoteAlbum = mockk<ImageRetrieverRemoteAlbum>()

    private val songGateway = mockk<SongGateway>()
    private val albumGateway = mockk<AlbumGateway>()
    private val artistGateway = mockk<ArtistGateway>()

    private val sut = ImageRetrieverRepository(
        localTrack = localTrack,
        remoteTrack = remoteTrack,
        localArtist = localArtist,
        remoteArtist = remoteArtist,
        localAlbum = localAlbum,
        remoteAlbum = remoteAlbum,
        songGateway = songGateway,
        albumGateway = albumGateway,
        artistGateway = artistGateway
    )

    @Nested
    inner class TrackTest {

        @Test
        fun `should delegate mustFetch`() = coroutineRule {
            coEvery { sut.mustFetchTrack(1L) } returns true

            val actual = sut.mustFetchTrack(1L)

            assertThat(actual).isEqualTo(true)
        }

        @Test
        fun `should delegate delete`() = coroutineRule {
            coEvery { sut.deleteTrack(1L) } returns Unit

            sut.deleteTrack(1L)

            coVerify { sut.deleteTrack(1L) }
        }

        @Test
        fun `should return cached`() = coroutineRule {
            val result = LastFmTrack.EMPTY
            coEvery { localTrack.getCached(1L) } returns result

            val actual = sut.getTrack(1L)

            assertThat(actual).isEqualTo(result)
        }

        @Test
        fun `should return null, no cached and not found in repo`() = coroutineRule {
            coEvery { localTrack.getCached(1L) } returns null
            coEvery { songGateway.getByParam(1L) } returns null

            val actual = sut.getTrack(1L)

            assertThat(actual).isNull()
        }

        @Test
        fun `should return fetched model and cache it, when item is not cached and is found in repo`() = coroutineRule {
            val song = Song.EMPTY
            val result = LastFmTrack.EMPTY

            coEvery { localTrack.getCached(1L) } returns null
            coEvery { songGateway.getByParam(1L) } returns song
            coEvery { remoteTrack.fetch(song) } returns result

            val actual = sut.getTrack(1L)

            assertThat(actual).isEqualTo(result)

            coVerify {
                localTrack.cache(result)
            }
        }

    }

    @Nested
    inner class AlbumTest {

        @Test
        fun `should delegate mustFetch`() = coroutineRule {
            coEvery { sut.mustFetchAlbum(1L) } returns true

            val actual = sut.mustFetchAlbum(1L)

            assertThat(actual).isEqualTo(true)
        }

        @Test
        fun `should delegate delete`() = coroutineRule {
            coEvery { sut.deleteAlbum(1L) } returns Unit

            sut.deleteAlbum(1L)

            coVerify { sut.deleteAlbum(1L) }
        }

        @Test
        fun `should return cached when has not same name as folder`() = coroutineRule {
            val album = Album.EMPTY.copy(
                title = "title",
                hasSameNameAsFolder = false
            )

            val result = LastFmAlbum.EMPTY
            coEvery { albumGateway.getByParam(1L) } returns album
            coEvery { localAlbum.getCached(1L) } returns result

            val actual = sut.getAlbum(1L)

            assertThat(actual).isEqualTo(result)
        }

        @Test
        fun `should return null when has same name as folder`() = coroutineRule {
            val album = Album.EMPTY.copy(
                title = "title",
                hasSameNameAsFolder = true
            )

            coEvery { albumGateway.getByParam(1L) } returns album

            val actual = sut.getAlbum(1L)

            assertThat(actual).isNull()
        }

        @Test
        fun `should return null, no cached and not found in repo`() = coroutineRule {
            coEvery { localAlbum.getCached(1L) } returns null
            coEvery { albumGateway.getByParam(1L) } returns null

            val actual = sut.getAlbum(1L)

            assertThat(actual).isNull()
        }

        @Test
        fun `should return fetched model and cache it, when item is not cached and is found in repo`() = coroutineRule {
            val album = Album.EMPTY
            val result = LastFmAlbum.EMPTY

            coEvery { localAlbum.getCached(1L) } returns null
            coEvery { albumGateway.getByParam(1L) } returns album
            coEvery { remoteAlbum.fetch(album) } returns result

            val actual = sut.getAlbum(1L)

            assertThat(actual).isEqualTo(result)

            coVerify {
                localAlbum.cache(result)
            }
        }

    }

    @Nested
    inner class ArtistTest {

        @Test
        fun `should delegate mustFetch`() = coroutineRule {
            coEvery { sut.mustFetchArtist(1L) } returns true

            val actual = sut.mustFetchArtist(1L)

            assertThat(actual).isEqualTo(true)
        }

        @Test
        fun `should delegate delete`() = coroutineRule {
            coEvery { sut.deleteArtist(1L) } returns Unit

            sut.deleteArtist(1L)

            coVerify { sut.deleteArtist(1L) }
        }

        @Test
        fun `should return cached`() = coroutineRule {
            val result = LastFmArtist.EMPTY
            coEvery { localArtist.getCached(1L) } returns result

            val actual = sut.getArtist(1L)

            assertThat(actual).isEqualTo(result)
        }

        @Test
        fun `should return null, no cached and not found in repo`() = coroutineRule {
            coEvery { localArtist.getCached(1L) } returns null
            coEvery { artistGateway.getByParam(1L) } returns null

            val actual = sut.getArtist(1L)

            assertThat(actual).isNull()
        }

        @Test
        fun `should return fetched model and cache it, when item is not cached and is found in repo`() = coroutineRule {
            val artist = Artist.EMPTY
            val result = LastFmArtist.EMPTY

            coEvery { localArtist.getCached(1L) } returns null
            coEvery { artistGateway.getByParam(1L) } returns artist
            coEvery { remoteArtist.fetch(artist) } returns result

            val actual = sut.getArtist(1L)

            assertThat(actual).isEqualTo(result)

            coVerify {
                localArtist.cache(result)
            }
        }

    }

}