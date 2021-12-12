package dev.olog.data.folder

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.olog.core.MediaId
import dev.olog.core.MediaIdCategory
import dev.olog.core.entity.MostPlayedSong
import dev.olog.core.entity.sort.*
import dev.olog.core.entity.track.Artist
import dev.olog.core.entity.track.Folder
import dev.olog.core.entity.track.Song
import dev.olog.data.ArtistView
import dev.olog.data.FolderView
import dev.olog.data.IndexedPlayables
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

        val actual = sut.getAll()
        val expected = Folder(
            directory = "dir",
            songs = 2,
        )

        Assert.assertEquals(listOf(expected), actual)
    }

    @Test
    fun `test observeAll`() = runTest {
        val query = QueryList(FolderView(directory = "dir", songs = 2))
        whenever(queries.selectAllSorted()).thenReturn(query)

        val expected = Folder(
            directory = "dir",
            songs = 2,
        )

        sut.observeAll().test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test getByParam`() {
        val query = QueryOneOrNull(FolderView(directory = "dir", songs = 2))
        whenever(queries.selectById("dir")).thenReturn(query)

        val actual = sut.getByParam("dir")
        val expected = Folder(
            directory = "dir",
            songs = 2,
        )

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test getByParam, missing item should return null`() {
        val query = QueryOneOrNull<Folders_view>(null)
        whenever(queries.selectById("dir")).thenReturn(query)

        val actual = sut.getByParam("dir")
        Assert.assertEquals(null, actual)
    }

    @Test
    fun `test observeByParam`() = runTest {
        val query = QueryOneOrNull(FolderView(directory = "dir", songs = 2))
        whenever(queries.selectById("dir")).thenReturn(query)

        val expected = Folder(
            directory = "dir",
            songs = 2,
        )

        sut.observeByParam("dir").test(this) {
            assertValue(expected)
        }
    }

    @Test
    fun `test observeByParam, missing item should return null`() = runTest {
        val query = QueryOneOrNull<Folders_view>(null)
        whenever(queries.selectById("dir")).thenReturn(query)

        sut.observeByParam("dir").test(this) {
            assertValue(null)
        }
    }

    @Test
    fun `test getTrackListByParam`() {
        val query = QueryList(IndexedPlayables(id = 1, is_podcast = false))
        whenever(queries.selectTracksByIdSorted("dir")).thenReturn(query)

        val actual = sut.getTrackListByParam("dir")
        val expected = Song(
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
        )

        Assert.assertEquals(listOf(expected), actual)
    }

    @Test
    fun `test observeTrackListByParam`() = runTest {
        val query = QueryList(IndexedPlayables(id = 1, is_podcast = false))
        whenever(queries.selectTracksByIdSorted("dir")).thenReturn(query)

        val expected = Song(
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
        )

        sut.observeTrackListByParam("dir").test(this) {
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

        val mediaId = MediaId.createCategoryValue(MediaIdCategory.FOLDERS, "dir")
        sut.observeMostPlayed(mediaId).test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test insertMostPlayed`() = runTest {
        val mediaId = MediaId.playableItem(
            parentId = MediaId.createCategoryValue(MediaIdCategory.FOLDERS, "dir"),
            songId = 1
        )
        sut.insertMostPlayed(mediaId)
        verify(queries).incrementMostPlayed(1, "dir")
    }

    @Test
    fun `test observeRecentlyAddedSongs`() = runTest {
        val query = QueryList(IndexedPlayables(id = 1, is_podcast = false))
        whenever(queries.selectRecentlyAddedSongs("dir")).thenReturn(query)

        val expected = Song(
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
        )

        sut.observeRecentlyAddedSongs("dir").test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test observeRelatedArtists`() = runTest {
        val query = QueryList(ArtistView(id = 1, songs = 2))
        whenever(queries.selectRelatedArtists("dir")).thenReturn(query)

        sut.observeRelatedArtists("dir").test(this) {
            assertValue(listOf(
                Artist(id = 1, name = "", songs = 2, isPodcast = false)
            ))
        }
    }

    @Test
    fun `test observeSiblings, should be null when item is missing`() = runTest {
        val query = QueryOneOrNull<Folders_view>(null)
        whenever(queries.selectById("dir")).thenReturn(query)

        sut.observeSiblings("dir").test(this) {
            assertNoValues()
        }
    }

    @Test
    fun `test getAllBlacklistedIncluded`() = runTest {
        val query = QueryOneOrNull(SelectAllBlacklistedIncluded(directory = "dir", songs = 2, date_added = 100))
        whenever(queries.selectAllBlacklistedIncluded()).thenReturn(query)

        val actual = sut.getAllBlacklistedIncluded()
        val expected = Folder(
            directory = "dir",
            songs = 2
        )
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test getSort`() {
        val sort = Sort(GenericSort.Title, SortDirection.ASCENDING)
        val query = QueryOne(sort)
        whenever(sortDao.getFoldersSortQuery()).thenReturn(query)
        Assert.assertEquals(sort, sut.getSort())
    }

    @Test
    fun `test setSort`() {
        val sort = Sort(GenericSort.Title, SortDirection.ASCENDING)
        sut.setSort(sort)
        verify(sortDao).setFoldersSort(sort)
    }

    @Test
    fun `test getDetailSort`() {
        val sort = Sort(FolderDetailSort.Title, SortDirection.ASCENDING)
        val query = QueryOne(sort)
        whenever(sortDao.getDetailFoldersSortQuery()).thenReturn(query)
        Assert.assertEquals(sort, sut.getDetailSort())
    }

    @Test
    fun `test observeDetailSort`() = runTest {
        val sort = Sort(FolderDetailSort.Title, SortDirection.ASCENDING)
        val query = QueryOne(sort)
        whenever(sortDao.getDetailFoldersSortQuery()).thenReturn(query)

        sut.observeDetailSort().test(this) {
            assertValue(sort)
        }
    }

    @Test
    fun `test setDetailSort`() {
        val sort = Sort(FolderDetailSort.Title, SortDirection.ASCENDING)
        sut.setDetailSort(sort)
        verify(sortDao).setDetailFoldersSort(sort)
    }
    
}