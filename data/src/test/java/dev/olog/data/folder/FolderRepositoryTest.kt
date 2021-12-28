package dev.olog.data.folder

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.olog.core.*
import dev.olog.core.entity.MostPlayedSong
import dev.olog.core.entity.id.FolderIdentifier
import dev.olog.core.entity.id.PlayableIdentifier
import dev.olog.core.sort.FolderDetailSort
import dev.olog.core.sort.GenericSort
import dev.olog.core.sort.Sort
import dev.olog.core.sort.SortDirection
import dev.olog.testing.ArtistView
import dev.olog.testing.FolderView
import dev.olog.testing.IndexedPlayables
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

class FolderRepositoryTest {

    private val queries = mock<FoldersQueries>()
    private val sortDao = mock<SortDao>()
    private val sut = FolderRepository(
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
        val query = QueryList(FolderView(directory = "dir", songs = 2))
        whenever(queries.selectAllSorted()).thenReturn(query)

        val actual = sut.getAll(FolderIdentifier.Path())
        val expected = MediaStoreFolder(
            directory = "dir",
            songs = 2,
        )

        Assert.assertEquals(listOf(expected), actual)
    }

    @Test
    fun `test observeAll`() = runTest {
        val query = QueryList(FolderView(directory = "dir", songs = 2))
        whenever(queries.selectAllSorted()).thenReturn(query)

        val expected = MediaStoreFolder(
            directory = "dir",
            songs = 2,
        )

        sut.observeAll(FolderIdentifier.Path()).test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test getByParam`() {
        val query = QueryOneOrNull(FolderView(directory = "dir", songs = 2))
        whenever(queries.selectById("dir")).thenReturn(query)

        val actual = sut.getById(FolderIdentifier.Path("dir"))
        val expected = MediaStoreFolder(
            directory = "dir",
            songs = 2,
        )

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test getByParam, missing item should return null`() {
        val query = QueryOneOrNull<Folders_view>(null)
        whenever(queries.selectById("dir")).thenReturn(query)

        val actual = sut.getById(FolderIdentifier.Path("dir"))
        Assert.assertEquals(null, actual)
    }

    @Test
    fun `test observeByParam`() = runTest {
        val query = QueryOneOrNull(FolderView(directory = "dir", songs = 2))
        whenever(queries.selectById("dir")).thenReturn(query)

        val expected = MediaStoreFolder(
            directory = "dir",
            songs = 2,
        )

        sut.observeById(FolderIdentifier.Path("dir")).test(this) {
            assertValue(expected)
        }
    }

    @Test
    fun `test observeByParam, missing item should return null`() = runTest {
        val query = QueryOneOrNull<Folders_view>(null)
        whenever(queries.selectById("dir")).thenReturn(query)

        sut.observeById(FolderIdentifier.Path("dir")).test(this) {
            assertValue(null)
        }
    }

    @Test
    fun `test getTrackListByParam`() {
        val query = QueryList(IndexedPlayables(id = 1, is_podcast = false))
        whenever(queries.selectTracksByIdSorted("dir")).thenReturn(query)

        val actual = sut.getPlayablesById(FolderIdentifier.Path("dir"))
        val expected = MediaStoreSong(id = 1)

        Assert.assertEquals(listOf(expected), actual)
    }

    @Test
    fun `test observeTrackListByParam`() = runTest {
        val query = QueryList(IndexedPlayables(id = 1, is_podcast = false))
        whenever(queries.selectTracksByIdSorted("dir")).thenReturn(query)

        val expected = MediaStoreSong(id = 1)

        sut.observePlayablesById(FolderIdentifier.Path("dir")).test(this) {
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
        whenever(queries.selectMostPlayed("dir")).thenReturn(query)

        val expected = MostPlayedSong(
            MediaStoreSong(id = 1),
            counter = 100
        )

        sut.observeMostPlayed(FolderIdentifier.Path("dir")).test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test insertMostPlayed`() = runTest {
        sut.insertMostPlayed(FolderIdentifier.Path("dir"), PlayableIdentifier.MediaStore(1, false))
        verify(queries).incrementMostPlayed(1, "dir")
    }

    @Test
    fun `test observeRecentlyAddedSongs`() = runTest {
        val query = QueryList(IndexedPlayables(id = 1, is_podcast = false))
        whenever(queries.selectRecentlyAddedSongs("dir")).thenReturn(query)

        val expected = MediaStoreSong(id = 1)

        sut.observeRecentlyAddedPlayablesById(FolderIdentifier.Path("dir")).test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test observeRelatedArtists`() = runTest {
        val query = QueryList(ArtistView(id = 1, songs = 2))
        whenever(queries.selectRelatedArtists("dir")).thenReturn(query)

        sut.observeRelatedArtistsById(FolderIdentifier.Path("dir")).test(this) {
            assertValue(listOf(
                MediaStoreArtist(id = 1, songs = 2)
            ))
        }
    }

    @Test
    fun `test observeSiblings`() = runTest {
        val query = QueryList(FolderView(directory = "dir", songs = 0))
        whenever(queries.selectSiblings("dir")).thenReturn(query)

        sut.observeSiblingsById(FolderIdentifier.Path("dir")).test(this) {
            assertValue(listOf(MediaStoreFolder(directory = "dir", songs = 0)))
        }
    }

    @Test
    fun `test getAllBlacklistedIncluded`() = runTest {
        val query = QueryList(SelectAllBlacklistedIncluded(directory = "dir", songs = 2, date_added = 100))
        whenever(queries.selectAllBlacklistedIncluded()).thenReturn(query)

        val actual = sut.getAllBlacklistedIncluded()
        val expected = MediaStoreFolder(
            directory = "dir",
            songs = 2
        )
        Assert.assertEquals(listOf(expected), actual)
    }

    @Test
    fun `test getSort`() {
        val sort = Sort(GenericSort.Title, SortDirection.ASCENDING)
        val query = QueryOne(sort)
        whenever(sortDao.getFoldersSortQuery()).thenReturn(query)
        Assert.assertEquals(sort, sut.getSort(FolderIdentifier.Path()))
    }

    @Test
    fun `test setSort`() {
        val sort = Sort(GenericSort.Title, SortDirection.ASCENDING)
        sut.setSort(FolderIdentifier.Path(), sort)
        verify(sortDao).setFoldersSort(sort)
    }

    @Test
    fun `test getDetailSort`() {
        val sort = Sort(FolderDetailSort.Title, SortDirection.ASCENDING)
        val query = QueryOne(sort)
        whenever(sortDao.getDetailFoldersSortQuery()).thenReturn(query)
        Assert.assertEquals(sort, sut.getDetailSort(FolderIdentifier.Path()))
    }

    @Test
    fun `test observeDetailSort`() = runTest {
        val sort = Sort(FolderDetailSort.Title, SortDirection.ASCENDING)
        val query = QueryOne(sort)
        whenever(sortDao.getDetailFoldersSortQuery()).thenReturn(query)

        sut.observeDetailSort(FolderIdentifier.Path()).test(this) {
            assertValue(sort)
        }
    }

    @Test
    fun `test setDetailSort`() {
        val sort = Sort(FolderDetailSort.Title, SortDirection.ASCENDING)
        sut.setDetailSort(FolderIdentifier.Path(), sort)
        verify(sortDao).setDetailFoldersSort(sort)
    }
    
}