package dev.olog.data.collection

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.olog.core.DateTimeFactory
import dev.olog.core.MediaStorePodcastCollection
import dev.olog.core.MediaStorePodcastEpisode
import dev.olog.core.entity.id.AuthorIdentifier
import dev.olog.core.entity.id.CollectionIdentifier
import dev.olog.core.sort.CollectionDetailSort
import dev.olog.core.sort.CollectionSort
import dev.olog.core.sort.Sort
import dev.olog.core.sort.SortDirection
import dev.olog.data.extensions.QueryList
import dev.olog.data.extensions.QueryOne
import dev.olog.data.extensions.QueryOneOrNull
import dev.olog.data.extensions.mockTransacter
import dev.olog.data.sort.SortDao
import dev.olog.flow.test.observer.test
import dev.olog.test.shared.TestSchedulers
import dev.olog.testing.IndexedPlayables
import dev.olog.testing.PodcastCollectionView
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class MediaStorePodcastCollectionRepositoryTest {

    private val queries = mock<PodcastCollectionQueries>()
    private val sortDao = mock<SortDao>()
    private val dateTimeFactory = mock<DateTimeFactory>()
    private val sut = MediaStorePodcastCollectionRepository(
        schedulers = TestSchedulers(),
        queries = queries,
        sortDao = sortDao,
        dateTimeFactory = dateTimeFactory,
    )

    @Before
    fun setup() {
        mockTransacter(queries)
    }

    @Test
    fun `test getAll`() {
        val query = QueryList(PodcastCollectionView(id = 1, songs = 2))
        whenever(queries.selectAllSorted()).thenReturn(query)

        val actual = sut.getAll()
        val expected = MediaStorePodcastCollection(
            id = 1,
            songs = 2,
        )

        Assert.assertEquals(listOf(expected), actual)
    }

    @Test
    fun `test observeAll`() = runTest {
        val query = QueryList(PodcastCollectionView(id = 1, songs = 2))
        whenever(queries.selectAllSorted()).thenReturn(query)

        val expected = MediaStorePodcastCollection(
            id = 1,
            songs = 2,
        )

        sut.observeAll().test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test getByParam`() {
        val query = QueryOneOrNull(PodcastCollectionView(id = 1, songs = 2))
        whenever(queries.selectById(1)).thenReturn(query)

        val actual = sut.getById(CollectionIdentifier.MediaStore(1, true))
        val expected = MediaStorePodcastCollection(
            id = 1,
            songs = 2,
        )

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test getByParam, missing item should return null`() {
        val query = QueryOneOrNull<Podcast_collections_view>(null)
        whenever(queries.selectById(1)).thenReturn(query)

        val actual = sut.getById(CollectionIdentifier.MediaStore(1, true))
        Assert.assertEquals(null, actual)
    }

    @Test
    fun `test observeByParam`() = runTest {
        val query = QueryOneOrNull(PodcastCollectionView(id = 1, songs = 2))
        whenever(queries.selectById(1)).thenReturn(query)

        val expected = MediaStorePodcastCollection(
            id = 1,
            songs = 2,
        )

        sut.observeById(CollectionIdentifier.MediaStore(1, true)).test(this) {
            assertValue(expected)
        }
    }

    @Test
    fun `test observeByParam, missing item should return null`() = runTest {
        val query = QueryOneOrNull<Podcast_collections_view>(null)
        whenever(queries.selectById(1)).thenReturn(query)

        sut.observeById(CollectionIdentifier.MediaStore(1, true)).test(this) {
            assertValue(null)
        }
    }

    @Test
    fun `test getTrackListByParam`() {
        val query = QueryList(IndexedPlayables(id = 1, is_podcast = true))
        whenever(queries.selectTracksByIdSorted(1)).thenReturn(query)

        val actual = sut.getPlayablesById(CollectionIdentifier.MediaStore(1, true))
        val expected = MediaStorePodcastEpisode(id = 1)

        Assert.assertEquals(listOf(expected), actual)
    }

    @Test
    fun `test observeTrackListByParam`() = runTest {
        val query = QueryList(IndexedPlayables(id = 1, is_podcast = true))
        whenever(queries.selectTracksByIdSorted(1)).thenReturn(query)

        val expected = MediaStorePodcastEpisode(id = 1)

        sut.observePlayablesById(CollectionIdentifier.MediaStore(1, true)).test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test observeRecentlyPlayed`() = runTest {
        val query = QueryList(PodcastCollectionView(id = 1, songs = 2))
        whenever(queries.selectRecentlyPlayed()).thenReturn(query)

        val expected = MediaStorePodcastCollection(
            id = 1,
            songs = 2,
        )

        sut.observeRecentlyPlayed().test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test addRecentlyPlayed`() = runTest {
        whenever(dateTimeFactory.currentTimeMillis()).thenReturn(100)

        sut.addToRecentlyPlayed(CollectionIdentifier.MediaStore(10, true))

        verify(queries).insertRecentlyPlayed(10, 100)
    }

    @Test
    fun `test observeRecentlyAdded`() = runTest {
        val query = QueryList(PodcastCollectionView(id = 1, songs = 2))
        whenever(queries.selectRecentlyAdded()).thenReturn(query)

        val expected = MediaStorePodcastCollection(
            id = 1,
            songs = 2,
        )

        sut.observeRecentlyAdded().test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test observeSiblings`() = runTest {
        val query = QueryOneOrNull(PodcastCollectionView(id = 1, author_id = 2, songs = 3))
        whenever(queries.selectById(1)).thenReturn(query)

        val artistAlbumsQuery = QueryList(
            PodcastCollectionView(id = 1, songs = 2),
            PodcastCollectionView(id = 2, songs = 4),
            PodcastCollectionView(id = 3, songs = 3),
        )
        whenever(queries.selectArtistAlbums(2)).thenReturn(artistAlbumsQuery)

        sut.observeSiblingsById(CollectionIdentifier.MediaStore(1, true)).test(this) {
            assertValue(listOf(
                MediaStorePodcastCollection(id = 2, songs = 4),
                MediaStorePodcastCollection(id = 3, songs = 3),
            ))
        }
    }

    @Test
    fun `test observeSiblings, should be null when item is missing`() = runTest {
        val query = QueryOneOrNull<Podcast_collections_view>(null)
        whenever(queries.selectById(1)).thenReturn(query)

        sut.observeSiblingsById(CollectionIdentifier.MediaStore(1, true)).test(this) {
            assertNoValues()
        }
    }

    @Test
    fun `test observeArtistsAlbums`() = runTest {
        val query = QueryList(PodcastCollectionView(id = 1, songs = 2))
        whenever(queries.selectArtistAlbums(1)).thenReturn(query)

        val expected = MediaStorePodcastCollection(
            id = 1,
            songs = 2,
        )

        sut.observeArtistsAlbums(AuthorIdentifier.MediaStore(1, true)).test(this) {
            assertValue(listOf(expected))
        }
    }

    @Test
    fun `test getSort`() {
        val sort = Sort(CollectionSort.Title, SortDirection.ASCENDING)
        val query = QueryOne(sort)
        whenever(sortDao.getPodcastCollectionsSortQuery()).thenReturn(query)
        Assert.assertEquals(sort, sut.getSort())
    }

    @Test
    fun `test setSort`() {
        val sort = Sort(CollectionSort.Title, SortDirection.ASCENDING)
        sut.setSort(sort)
        verify(sortDao).setPodcastCollectionsSort(sort)
    }

    @Test
    fun `test getDetailSort`() {
        val sort = Sort(CollectionDetailSort.Title, SortDirection.ASCENDING)
        val query = QueryOne(sort)
        whenever(sortDao.getDetailPodcastCollectionsSortQuery()).thenReturn(query)
        Assert.assertEquals(sort, sut.getDetailSort())
    }

    @Test
    fun `test observeDetailSort`() = runTest {
        val sort = Sort(CollectionDetailSort.Title, SortDirection.ASCENDING)
        val query = QueryOne(sort)
        whenever(sortDao.getDetailPodcastCollectionsSortQuery()).thenReturn(query)

        sut.observeDetailSort().test(this) {
            assertValue(sort)
        }
    }

    @Test
    fun `test setDetailSort`() {
        val sort = Sort(CollectionDetailSort.Title, SortDirection.ASCENDING)
        sut.setDetailSort(sort)
        verify(sortDao).setDetailPodcastCollectionsSort(sort)
    }

}