package dev.olog.data.db

import dev.olog.data.DatabaseBuilder
import dev.olog.data.model.db.FavoriteEntity
import dev.olog.data.model.db.FavoritePodcastEntity
import dev.olog.test.shared.MainCoroutineRule
import dev.olog.test.shared.runBlockingTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
internal class FavoriteDaoTest {

    private val mockFavorite1 = FavoriteEntity(1)
    private val mockFavorite2 = FavoriteEntity(2)
    private val mockPodcastFavorite1 = FavoritePodcastEntity(1)
    private val mockPodcastFavorite2 = FavoritePodcastEntity(2)

    @get:Rule
    val coroutinesRule = MainCoroutineRule()

    private val db by lazy { DatabaseBuilder.build(coroutinesRule.testDispatcher) }
    private val sut by lazy { db.favoriteDao() }

    @Before
    fun setUp() = coroutinesRule.runBlockingTest {
        sut.insertGroupImpl(listOf(mockFavorite1, mockFavorite2))
        sut.insertGroupPodcastImpl(listOf(mockPodcastFavorite1, mockPodcastFavorite2))
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun testGetAll() {
        // when
        val actual = sut.getAllTracksImpl()

        // then
        assertEquals(
            listOf(mockFavorite1, mockFavorite2),
            actual.map { FavoriteEntity(it) }
        )
    }

    @Test
    fun testGetAllPodcasts() {
        // when
        val actual = sut.getAllPodcastsImpl()

        // then
        assertEquals(
            listOf(mockPodcastFavorite1, mockPodcastFavorite2),
            actual.map { FavoritePodcastEntity(it) }
        )
    }

    @Test
    fun testObserveAll() = coroutinesRule.runBlockingTest {
        // when
        val actual = sut.observeAllTracksImpl()
            .take(1)
            .first()

        // then
        assertEquals(
            listOf(mockFavorite1, mockFavorite2),
            actual.map { FavoriteEntity(it) }
        )
    }

    @Test
    fun testObserveAllPodcasts() = coroutinesRule.runBlockingTest {
        // when
        val actual = sut.observeAllPodcastsImpl()
            .take(1)
            .first()

        // then
        assertEquals(
            listOf(mockPodcastFavorite1, mockPodcastFavorite2),
            actual.map { FavoritePodcastEntity(it) }
        )
    }

    @Test
    fun shouldDeleteAll() {
        // when
        sut.deleteAllTracks()

        // then
        assertEquals(emptyList<Long>(), sut.getAllTracksImpl())
    }

    @Test
    fun shouldDeleteAllPodcast() {
        // when
        sut.deleteAllPodcasts()

        // then
        assertEquals(emptyList<Long>(), sut.getAllPodcastsImpl())
    }

    @Test
    fun shouldInsert() = coroutinesRule.runBlockingTest {
        // given
        val item = mockFavorite1.copy(songId = 10)

        // when
        val insertedId = sut.insertOneImpl(item)

        // then
        assertEquals(
            item.songId,
            insertedId
        )
    }

    @Test
    fun shouldNotInsertSameItem() = coroutinesRule.runBlockingTest {
        // given
        val item = mockFavorite1

        // when
        val insertedId = sut.insertOneImpl(item)

        // then
        assertEquals(
            -1,
            insertedId
        )
    }

    @Test
    fun shouldInsertPodcast() = coroutinesRule.runBlockingTest {
        // given
        val item = mockPodcastFavorite1.copy(podcastId = 10)

        // when
        val insertedId = sut.insertOnePodcastImpl(item)

        // then
        assertEquals(
            item.podcastId,
            insertedId
        )
    }

    @Test
    fun shouldNotInsertSamePodcastItem() = coroutinesRule.runBlockingTest {
        // given
        val item = mockPodcastFavorite1

        // when
        val insertedId = sut.insertOnePodcastImpl(item)

        // then
        assertEquals(
            -1,
            insertedId
        )
    }

    @Test
    fun shouldInsertGroup() = coroutinesRule.runBlockingTest {
        // given
        val list = listOf(
            mockFavorite1.copy(songId = 10),
            mockFavorite1.copy(songId = 11)
        )

        // when
        val insertedIds = sut.insertGroupImpl(list)

        // then
        assertEquals(
            list.map { it.songId }.sorted(),
            insertedIds.sorted()
        )
    }

    @Test
    fun shouldInsertOnlyOne() = coroutinesRule.runBlockingTest {
        // given
        val list = listOf(
            mockFavorite1,
            mockFavorite1.copy(songId = 10)
        )

        // when
        val insertedId = sut.insertGroupImpl(list)

        // then
        assertEquals(
            listOf(-1L, 10L),
            insertedId
        )
    }

    @Test
    fun shouldInsertPodcastGroup() = coroutinesRule.runBlockingTest {
        // given
        val list = listOf(
            mockPodcastFavorite1.copy(podcastId = 10),
            mockPodcastFavorite1.copy(podcastId = 11)
        )

        // when
        val insertedIds = sut.insertGroupPodcastImpl(list)

        // then
        assertEquals(
            list.map { it.podcastId }.sorted(),
            insertedIds.sorted()
        )
    }

    @Test
    fun shouldInsertPodcastOnlyOne() = coroutinesRule.runBlockingTest {
        // given
        val list = listOf(
            mockPodcastFavorite1,
            mockPodcastFavorite1.copy(podcastId = 10)
        )

        // when
        val insertedId = sut.insertGroupPodcastImpl(list)

        // then
        assertEquals(
            listOf(-1L, 10L),
            insertedId
        )
    }

    @Test
    fun shouldDeleteOnlyOne() = coroutinesRule.runBlockingTest {
        // when
        val deleted = sut.deleteGroupImpl(listOf(mockFavorite1))

        // then
        assertEquals(
            1,
            deleted
        )
    }

    @Test
    fun shouldDeleteOnlyOnePodcast() = coroutinesRule.runBlockingTest {
        // when
        val deleted = sut.deleteGroupPodcastImpl(listOf(mockPodcastFavorite1))

        // then
        assertEquals(
            1,
            deleted
        )
    }

    @Test
    fun testGetTrack() = coroutinesRule.runBlockingTest {
        // given
        val id = mockFavorite1.songId

        // when
        val item = sut.getTrackById(id)

        // then
        assertNotNull(item)
    }

    @Test
    fun testGetPodcast() = coroutinesRule.runBlockingTest {
        // given
        val id = mockPodcastFavorite1.podcastId

        // when
        val item = sut.getPodcastById(id)

        // then
        assertNotNull(item)
    }

    @Test
    fun testIsFavorite() = coroutinesRule.runBlockingTest {
        // given
        val id = mockFavorite1.songId

        // when
        val favorite = sut.isFavorite(id)

        // then
        assertTrue(favorite)
    }

    @Test
    fun testIsNotFavorite() = coroutinesRule.runBlockingTest {
        // given
        val id = -1L

        // when
        val favorite = sut.isFavorite(id)

        // then
        assertFalse(favorite)
    }

    @Test
    fun testPodcastIsFavorite() = coroutinesRule.runBlockingTest {
        // given
        val id = mockPodcastFavorite1.podcastId

        // when
        val favorite = sut.isFavoritePodcast(id)

        // then
        assertTrue(favorite)
    }

    @Test
    fun testPodcastIsNotFavorite() = coroutinesRule.runBlockingTest {
        // given
        val id = -1L

        // when
        val favorite = sut.isFavoritePodcast(id)

        // then
        assertFalse(favorite)
    }

}