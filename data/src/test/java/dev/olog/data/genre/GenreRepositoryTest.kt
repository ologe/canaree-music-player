package dev.olog.data.genre

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.olog.core.MediaStoreArtist
import dev.olog.core.MediaStoreGenre
import dev.olog.core.MediaStoreSong
import dev.olog.core.entity.MostPlayedSong
import dev.olog.core.entity.id.GenreIdentifier
import dev.olog.core.entity.id.PlayableIdentifier
import dev.olog.core.sort.GenericSort
import dev.olog.core.sort.GenreDetailSort
import dev.olog.core.sort.Sort
import dev.olog.core.sort.SortDirection
import dev.olog.data.extensions.QueryList
import dev.olog.data.extensions.QueryOne
import dev.olog.data.extensions.QueryOneOrNull
import dev.olog.data.extensions.mockTransacter
import dev.olog.data.sort.SortDao
import dev.olog.flow.test.observer.test
import dev.olog.test.shared.TestSchedulers
import dev.olog.testing.GenreView
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

        val actual = sut.getAll(GenreIdentifier.MediaStore())
        val expected = MediaStoreGenre(
            id = 1,
            songs = 2,
        )

        Assert.assertEquals(listOf(expected), actual)
    }

    @Test
    fun `test observeAll`() = runTest {
        val query = QueryList(GenreView(id = 1, songs = 2))
        whenever(queries.selectAllSorted()).thenReturn(query)

        val expected = MediaStoreGenre(
            id = 1,
            songs = 2,
        )

        sut.observeAll(GenreIdentifier.MediaStore()).test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test getByParam`() {
        val query = QueryOneOrNull(GenreView(id = 1, songs = 2))
        whenever(queries.selectById(1)).thenReturn(query)

        val actual = sut.getById(GenreIdentifier.MediaStore(1))
        val expected = MediaStoreGenre(
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

        val actual = sut.getById(GenreIdentifier.MediaStore(1))
        Assert.assertEquals(null, actual)
    }

    @Test
    fun `test observeByParam`() = runTest {
        val query = QueryOneOrNull(GenreView(id = 1, songs = 2))
        whenever(queries.selectById(1)).thenReturn(query)

        val expected = MediaStoreGenre(
            id = 1,
            songs = 2,
        )

        sut.observeById(GenreIdentifier.MediaStore(1)).test(this) {
            assertValue(expected)
        }
    }

    @Test
    fun `test observeByParam, missing item should return null`() = runTest {
        val query = QueryOneOrNull<Genres_view>(null)
        whenever(queries.selectById(1)).thenReturn(query)

        sut.observeById(GenreIdentifier.MediaStore(1)).test(this) {
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

        val actual = sut.getPlayablesById(GenreIdentifier.MediaStore(1))
        val expected = MediaStoreSong(id = 2)

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

        val expected = MediaStoreSong(id = 2)

        sut.observePlayablesById(GenreIdentifier.MediaStore(1)).test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test observeMostPlayed`() = runTest {
        val item = SelectMostPlayed(
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
            counter = 100
        )
        val query = QueryList(item)
        whenever(queries.selectMostPlayed(1)).thenReturn(query)

        val expected = MostPlayedSong(
            song = MediaStoreSong(id = 2),
            counter = 100
        )

        sut.observeMostPlayed(GenreIdentifier.MediaStore(1)).test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test insertMostPlayed`() = runTest {
        sut.insertMostPlayed(GenreIdentifier.MediaStore(1), PlayableIdentifier.MediaStore(2, false))
        verify(queries).incrementMostPlayed(2, 1)
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

        val expected = MediaStoreSong(id = 2)

        sut.observeRecentlyAddedPlayablesById(GenreIdentifier.MediaStore(1)).test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test observeRelatedArtists`() = runTest {
        val query = QueryList(SelectRelatedArtists(author_id = 1, songs = 2, author = "", album_artist = ""))
        whenever(queries.selectRelatedArtists(1)).thenReturn(query)

        sut.observeRelatedArtistsById(GenreIdentifier.MediaStore(1)).test(this) {
            assertValue(listOf(
                MediaStoreArtist(id = 1, songs = 2)
            ))
        }
    }

    @Test
    fun `test observeSiblings`() = runTest {
        val query = QueryList(GenreView(id = 1L))
        whenever(queries.selectSiblings(1)).thenReturn(query)

        sut.observeSiblingsById(GenreIdentifier.MediaStore(1)).test(this) {
            assertValue(listOf(MediaStoreGenre(id = 1)))
        }
    }

    @Test
    fun `test getSort`() {
        val sort = Sort(GenericSort.Title, SortDirection.ASCENDING)
        val query = QueryOne(sort)
        whenever(sortDao.getGenresSortQuery()).thenReturn(query)
        Assert.assertEquals(sort, sut.getSort(GenreIdentifier.MediaStore()))
    }

    @Test
    fun `test setSort`() {
        val sort = Sort(GenericSort.Title, SortDirection.ASCENDING)
        sut.setSort(GenreIdentifier.MediaStore(), sort)
        verify(sortDao).setGenresSort(sort)
    }

    @Test
    fun `test getDetailSort`() {
        val sort = Sort(GenreDetailSort.Title, SortDirection.ASCENDING)
        val query = QueryOne(sort)
        whenever(sortDao.getDetailGenresSortQuery()).thenReturn(query)
        Assert.assertEquals(sort, sut.getDetailSort(GenreIdentifier.MediaStore()))
    }

    @Test
    fun `test observeDetailSort`() = runTest {
        val sort = Sort(GenreDetailSort.Title, SortDirection.ASCENDING)
        val query = QueryOne(sort)
        whenever(sortDao.getDetailGenresSortQuery()).thenReturn(query)

        sut.observeDetailSort(GenreIdentifier.MediaStore()).test(this) {
            assertValue(sort)
        }
    }

    @Test
    fun `test setDetailSort`() {
        val sort = Sort(GenreDetailSort.Title, SortDirection.ASCENDING)
        sut.setDetailSort(GenreIdentifier.MediaStore(), sort)
        verify(sortDao).setDetailGenresSort(sort)
    }
    
}