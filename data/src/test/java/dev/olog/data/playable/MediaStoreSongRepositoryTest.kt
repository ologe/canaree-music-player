package dev.olog.data.playable

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.olog.core.MediaStoreSong
import dev.olog.core.entity.id.CollectionIdentifier
import dev.olog.core.entity.id.PlayableIdentifier
import dev.olog.core.sort.PlayableSort
import dev.olog.core.sort.Sort
import dev.olog.core.sort.SortDirection
import dev.olog.data.extensions.QueryList
import dev.olog.data.extensions.QueryOne
import dev.olog.data.extensions.QueryOneOrNull
import dev.olog.data.extensions.mockTransacter
import dev.olog.data.index.Indexed_playables
import dev.olog.data.sort.SortDao
import dev.olog.flow.test.observer.test
import dev.olog.test.shared.TestSchedulers
import dev.olog.testing.emptyIndexedPlayables
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class MediaStoreSongRepositoryTest {

    private val queries = mock<SongsQueries>()
    private val sortDao = mock<SortDao>()
    private val repo = MediaStoreSongRepository(
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
        val query = QueryList(
            emptyIndexedPlayables().copy(id = 1L),
            emptyIndexedPlayables().copy(id = 2L),
        )
        whenever(queries.selectAllSorted()).thenReturn(query)

        val actual = repo.getAll()
        val expected = listOf(
            MediaStoreSong(id = 1L),
            MediaStoreSong(id = 2L),
        )

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test observeAll`() = runTest {
        val query = QueryList(
            emptyIndexedPlayables().copy(id = 1L),
            emptyIndexedPlayables().copy(id = 2L),
        )
        whenever(queries.selectAllSorted()).thenReturn(query)

        val expected = listOf(
            MediaStoreSong(id = 1L),
            MediaStoreSong(id = 2L),
        )

        repo.observeAll().test(this) {
            assertValue(expected)
        }
    }

    @Test
    fun `test getByParam`() {
        val query = QueryOneOrNull(
            emptyIndexedPlayables().copy(id = 1L),
        )
        whenever(queries.selectById(1)).thenReturn(query)

        val actual = repo.getById(PlayableIdentifier.MediaStore(1, false))
        val expected = MediaStoreSong(id = 1L)

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test getByParam, missing item should return null`() {
        val query = QueryOneOrNull<Indexed_playables>(null)
        whenever(queries.selectById(1)).thenReturn(query)

        val actual = repo.getById(PlayableIdentifier.MediaStore(1, false))
        Assert.assertEquals(null, actual)
    }

    @Test
    fun `test observeByParam`() = runTest {
        val query = QueryOneOrNull(
            emptyIndexedPlayables().copy(id = 1L),
        )
        whenever(queries.selectById(1)).thenReturn(query)

        val expected = MediaStoreSong(id = 1L)

        repo.observeById(PlayableIdentifier.MediaStore(1, false)).test(this) {
            assertValue(expected)
        }
    }

    @Test
    fun `test observeByParam, missing item should return null`() = runTest {
        val query = QueryOneOrNull<Indexed_playables>(null)
        whenever(queries.selectById(1)).thenReturn(query)

        repo.observeById(PlayableIdentifier.MediaStore(1, false)).test(this) {
            assertValue(null)
        }
    }

    @Test
    fun `test getByAlbumId`() {
        val query = QueryOneOrNull(
            emptyIndexedPlayables().copy(collection_id = 1L),
        )
        whenever(queries.selectByCollectionId(1)).thenReturn(query)

        val actual = repo.getByAlbumId(CollectionIdentifier.MediaStore(1, false))
        val expected = MediaStoreSong(collectionId = 1L)

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test getByAlbumId, missing item should return null`() {
        val query = QueryOneOrNull<Indexed_playables>(null)
        whenever(queries.selectByCollectionId(1)).thenReturn(query)

        val actual = repo.getByAlbumId(CollectionIdentifier.MediaStore(1, false))
        Assert.assertEquals(null, actual)
    }

    @Test
    fun `test getSort`() {
        val sort = Sort(PlayableSort.Title, SortDirection.ASCENDING)
        val query = QueryOne(sort)
        whenever(sortDao.getSongsSort()).thenReturn(query)

        val actual = repo.getSort()
        Assert.assertEquals(sort, actual)
    }

    @Test
    fun `test setSort`() {
        val sort = Sort(PlayableSort.Title, SortDirection.ASCENDING)
        repo.setSort(sort)
        verify(sortDao).setSongsSort(sort)
    }

}