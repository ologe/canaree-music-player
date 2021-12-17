package dev.olog.data.genre

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.MostPlayedSong
import dev.olog.core.entity.sort.GenericSort
import dev.olog.core.entity.sort.GenreDetailSort
import dev.olog.core.entity.sort.Sort
import dev.olog.core.entity.sort.SortDirection
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Genre
import dev.olog.core.entity.track.Song
import dev.olog.data.GenreView
import dev.olog.data.extensions.QueryList
import dev.olog.data.extensions.QueryOne
import dev.olog.data.extensions.QueryOneOrNull
import dev.olog.data.extensions.mockTransacter
import dev.olog.data.sort.SortDao
import dev.olog.flow.test.observer.test
import dev.olog.test.shared.TestSchedulers
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class GenreRepositoryTest {

    private val queries = mock<GenresQueries>()
    private val sortDao = mock<SortDao>()
    private val sut = GenreRepository(
        schedulers = TestSchedulers(),
        queries = queries,
        sortDao = sortDao,
    )

    @Before
    fun setup() {
        mockTransacter(queries)
    }

    @Test
    fun `test getAll`() {
        val query = QueryList(GenreView(id = 1, songs = 2))
        whenever(queries.selectAllSorted()).thenReturn(query)

        val actual = sut.getAll()
        val expected = Genre(
            id = 1,
            songs = 2,
            name = "",
        )

        Assert.assertEquals(listOf(expected), actual)
    }

    @Test
    fun `test observeAll`() = runTest {
        val query = QueryList(GenreView(id = 1, songs = 2))
        whenever(queries.selectAllSorted()).thenReturn(query)

        val expected = Genre(
            id = 1,
            songs = 2,
            name = "",
        )

        sut.observeAll().test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test getByParam`() {
        val query = QueryOneOrNull(GenreView(id = 1, songs = 2))
        whenever(queries.selectById(1)).thenReturn(query)

        val actual = sut.getByParam(1)
        val expected = Genre(
            id = 1,
            songs = 2,
            name = "",
        )

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test getByParam, missing item should return null`() {
        val query = QueryOneOrNull<Genres_view>(null)
        whenever(queries.selectById(1)).thenReturn(query)

        val actual = sut.getByParam(1)
        Assert.assertEquals(null, actual)
    }

    @Test
    fun `test observeByParam`() = runTest {
        val query = QueryOneOrNull(GenreView(id = 1, songs = 2))
        whenever(queries.selectById(1)).thenReturn(query)

        val expected = Genre(
            id = 1,
            songs = 2,
            name = "",
        )

        sut.observeByParam(1).test(this) {
            assertValue(expected)
        }
    }

    @Test
    fun `test observeByParam, missing item should return null`() = runTest {
        val query = QueryOneOrNull<Genres_view>(null)
        whenever(queries.selectById(1)).thenReturn(query)

        sut.observeByParam(1).test(this) {
            assertValue(null)
        }
    }

    @Test
    fun `test getTrackListByParam`() {
        val item = Genres_playables_view(
            id = 2,
            author_id = 0,
            collection_id = 0,
            title = "",
            author = "",
            album_artist = "",
            collection = "",
            duration = 0,
            date_added = 0,
            directory = "",
            path = "",
            disc_number = 0,
            track_number = 0,
            is_podcast = false,
            genre_id = 1
        )
        val query = QueryList(item)
        whenever(queries.selectTracksByIdSorted(1)).thenReturn(query)

        val actual = sut.getTrackListByParam(1)
        val expected = Song(
            id = 2,
            artistId = 0,
            albumId = 0,
            title = "",
            artist = "",
            albumArtist = "",
            album = "",
            duration = 0,
            dateAdded = 0,
            directory = "",
            path = "",
            discNumber = 0,
            trackNumber = 0,
            idInPlaylist = 0,
            isPodcast = false
        )

        Assert.assertEquals(listOf(expected), actual)
    }

    @Test
    fun `test observeTrackListByParam`() = runTest {
        val item = Genres_playables_view(
            id = 2,
            author_id = 0,
            collection_id = 0,
            title = "",
            author = "",
            album_artist = "",
            collection = "",
            duration = 0,
            date_added = 0,
            directory = "",
            path = "",
            disc_number = 0,
            track_number = 0,
            is_podcast = false,
            genre_id = 1
        )
        val query = QueryList(item)
        whenever(queries.selectTracksByIdSorted(1)).thenReturn(query)

        val expected = Song(
            id = 2,
            artistId = 0,
            albumId = 0,
            title = "",
            artist = "",
            albumArtist = "",
            album = "",
            duration = 0,
            dateAdded = 0,
            directory = "",
            path = "",
            discNumber = 0,
            trackNumber = 0,
            idInPlaylist = 0,
            isPodcast = false
        )

        sut.observeTrackListByParam(1).test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test observeMostPlayed`() = runTest {
        val item = SelectMostPlayed(
            id = 1,
            author_id = 0,
            collection_id = 0,
            title = "",
            author = "",
            album_artist = "",
            collection = "",
            duration = 0,
            date_added = 0,
            directory = "",
            path = "",
            disc_number = 0,
            track_number = 0,
            is_podcast = false,
            counter = 100
        )
        val query = QueryList(item)
        whenever(queries.selectMostPlayed(1)).thenReturn(query)

        val expected = MostPlayedSong(
            Song(
                id = 1,
                artistId = 0,
                albumId = 0,
                title = "",
                artist = "",
                albumArtist = "",
                album = "",
                duration = 0,
                dateAdded = 0,
                directory = "",
                path = "",
                discNumber = 0,
                trackNumber = 0,
                idInPlaylist = 0,
                isPodcast = false
            ),
            counter = 100
        )

        val mediaId = MediaId.createCategoryValue(MediaIdCategory.GENRES, "1")
        sut.observeMostPlayed(mediaId).test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test insertMostPlayed`() = runTest {
        val mediaId = MediaId.playableItem(
            parentId = MediaId.createCategoryValue(MediaIdCategory.GENRES, "1"),
            songId = 1
        )
        sut.insertMostPlayed(mediaId)
        verify(queries).incrementMostPlayed(1, 1)
    }

    @Test
    fun `test observeRecentlyAddedSongs`() = runTest {
        val item = Genres_playables_view(
            id = 2,
            author_id = 0,
            collection_id = 0,
            title = "",
            author = "",
            album_artist = "",
            collection = "",
            duration = 0,
            date_added = 0,
            directory = "",
            path = "",
            disc_number = 0,
            track_number = 0,
            is_podcast = false,
            genre_id = 1
        )
        val query = QueryList(item)
        whenever(queries.selectRecentlyAddedSongs(1)).thenReturn(query)

        val expected = Song(
            id = 2,
            artistId = 0,
            albumId = 0,
            title = "",
            artist = "",
            albumArtist = "",
            album = "",
            duration = 0,
            dateAdded = 0,
            directory = "",
            path = "",
            discNumber = 0,
            trackNumber = 0,
            idInPlaylist = 0,
            isPodcast = false
        )

        sut.observeRecentlyAddedSongs(1).test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test observeRelatedArtists`() = runTest {
        val query = QueryList(SelectRelatedArtists(author_id = 1, songs = 2, author = "", album_artist = ""))
        whenever(queries.selectRelatedArtists(1)).thenReturn(query)

        sut.observeRelatedArtists(1).test(this) {
            assertValue(listOf(
                Artist(id = 1, name = "", songs = 2, isPodcast = false)
            ))
        }
    }

    @Test
    fun `test observeSiblings`() = runTest {
        val query = QueryList(GenreView(id = 1L))
        whenever(queries.selectSiblings(1)).thenReturn(query)

        sut.observeSiblings(1).test(this) {
            assertValue(listOf(Genre(id = 1, name = "", songs = 0)))
        }
    }

    @Test
    fun `test getSort`() {
        val sort = Sort(GenericSort.Title, SortDirection.ASCENDING)
        val query = QueryOne(sort)
        whenever(sortDao.getGenresSortQuery()).thenReturn(query)
        Assert.assertEquals(sort, sut.getSort())
    }

    @Test
    fun `test setSort`() {
        val sort = Sort(GenericSort.Title, SortDirection.ASCENDING)
        sut.setSort(sort)
        verify(sortDao).setGenresSort(sort)
    }

    @Test
    fun `test getDetailSort`() {
        val sort = Sort(GenreDetailSort.Title, SortDirection.ASCENDING)
        val query = QueryOne(sort)
        whenever(sortDao.getDetailGenresSortQuery()).thenReturn(query)
        Assert.assertEquals(sort, sut.getDetailSort())
    }

    @Test
    fun `test observeDetailSort`() = runTest {
        val sort = Sort(GenreDetailSort.Title, SortDirection.ASCENDING)
        val query = QueryOne(sort)
        whenever(sortDao.getDetailGenresSortQuery()).thenReturn(query)

        sut.observeDetailSort().test(this) {
            assertValue(sort)
        }
    }

    @Test
    fun `test setDetailSort`() {
        val sort = Sort(GenreDetailSort.Title, SortDirection.ASCENDING)
        sut.setDetailSort(sort)
        verify(sortDao).setDetailGenresSort(sort)
    }
    
}