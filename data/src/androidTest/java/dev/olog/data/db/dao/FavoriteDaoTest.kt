package dev.olog.data.db.dao

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.runner.AndroidJUnit4
import dev.olog.data.db.entities.FavoriteEntity
import dev.olog.data.db.entities.FavoritePodcastEntity
import dev.olog.test.shared.MainCoroutineRule
import dev.olog.test.shared.runBlocking
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
internal class FavoriteDaoTest {

    private val mockFavorite1 = FavoriteEntity(1)
    private val mockFavorite2 = FavoriteEntity(2)
    private val mockPodcastFavorite1 = FavoritePodcastEntity(1)
    private val mockPodcastFavorite2 = FavoritePodcastEntity(2)

    lateinit var db: AppDatabase
    lateinit var dao: FavoriteDao

    @get:Rule
    val coroutinesRule = MainCoroutineRule()

    @Before
    fun setUp() = coroutinesRule.runBlocking {
        val context = ApplicationProvider.getApplicationContext<Application>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .setQueryExecutor(coroutinesRule.testDispatcher.asExecutor())
            .build()
        dao = db.favoriteDao()

        dao.insertGroupImpl(listOf(mockFavorite1, mockFavorite2))
        dao.insertGroupPodcastImpl(listOf(mockPodcastFavorite1, mockPodcastFavorite2))
    }

    @After
    @Throws(IOException::class)
    fun teardown() {
        db.close()
    }

    @Test
    fun testGetAll() {
        // when
        val actual = dao.getAllTracksImpl()

        // then
        assertEquals(
            listOf(mockFavorite1, mockFavorite2),
            actual.map { FavoriteEntity(it) }
        )
    }

    @Test
    fun testGetAllPodcasts() {
        // when
        val actual = dao.getAllPodcastsImpl()

        // then
        assertEquals(
            listOf(mockPodcastFavorite1, mockPodcastFavorite2),
            actual.map { FavoritePodcastEntity(it) }
        )
    }

    @Test
    fun testObserveAll() = coroutinesRule.runBlocking {
        // when
        val actual = dao.observeAllTracksImpl()
            .take(1)
            .first()

        // then
        assertEquals(
            listOf(mockFavorite1, mockFavorite2),
            actual.map { FavoriteEntity(it) }
        )
    }

    @Test
    fun testObserveAllPodcasts() = coroutinesRule.runBlocking {
        // when
        val actual = dao.observeAllPodcastsImpl()
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
        dao.deleteTracks()

        // then
        assertEquals(emptyList<Long>(), dao.getAllTracksImpl())
    }

    @Test
    fun shouldDeleteAllPodcast() {
        // when
        dao.deleteAllPodcasts()

        // then
        assertEquals(emptyList<Long>(), dao.getAllPodcastsImpl())
    }

    @Test
    fun shouldInsert() = coroutinesRule.runBlocking {
        // given
        val item = mockFavorite1.copy(songId = 10)

        // when
        val insertedId = dao.insertOneImpl(item)

        // then
        assertEquals(
            item.songId,
            insertedId
        )
    }

    @Test
    fun shouldNotInsertSameItem() = coroutinesRule.runBlocking {
        // given
        val item = mockFavorite1

        // when
        val insertedId = dao.insertOneImpl(item)

        // then
        assertEquals(
            -1,
            insertedId
        )
    }

    @Test
    fun shouldInsertPodcast() = coroutinesRule.runBlocking {
        // given
        val item = mockPodcastFavorite1.copy(podcastId = 10)

        // when
        val insertedId = dao.insertOnePodcastImpl(item)

        // then
        assertEquals(
            item.podcastId,
            insertedId
        )
    }

    @Test
    fun shouldNotInsertSamePodcastItem() = coroutinesRule.runBlocking {
        // given
        val item = mockPodcastFavorite1

        // when
        val insertedId = dao.insertOnePodcastImpl(item)

        // then
        assertEquals(
            -1,
            insertedId
        )
    }

    @Test
    fun shouldInsertGroup() = coroutinesRule.runBlocking {
        // given
        val list = listOf(
            mockFavorite1.copy(songId = 10),
            mockFavorite1.copy(songId = 11)
        )

        // when
        val insertedIds = dao.insertGroupImpl(list)

        // then
        assertEquals(
            list.map { it.songId }.sorted(),
            insertedIds.sorted()
        )
    }

    @Test
    fun shouldInsertOnlyOne() = coroutinesRule.runBlocking {
        // given
        val list = listOf(
            mockFavorite1,
            mockFavorite1.copy(songId = 10)
        )

        // when
        val insertedId = dao.insertGroupImpl(list)

        // then
        assertEquals(
            listOf(-1L, 10L),
            insertedId
        )
    }

    @Test
    fun shouldInsertPodcastGroup() = coroutinesRule.runBlocking {
        // given
        val list = listOf(
            mockPodcastFavorite1.copy(podcastId = 10),
            mockPodcastFavorite1.copy(podcastId = 11)
        )

        // when
        val insertedIds = dao.insertGroupPodcastImpl(list)

        // then
        assertEquals(
            list.map { it.podcastId }.sorted(),
            insertedIds.sorted()
        )
    }

    @Test
    fun shouldInsertPodcastOnlyOne() = coroutinesRule.runBlocking {
        // given
        val list = listOf(
            mockPodcastFavorite1,
            mockPodcastFavorite1.copy(podcastId = 10)
        )

        // when
        val insertedId = dao.insertGroupPodcastImpl(list)

        // then
        assertEquals(
            listOf(-1L, 10L),
            insertedId
        )
    }

    @Test
    fun shouldDeleteOnlyOne() = coroutinesRule.runBlocking {
        // when
        val deleted = dao.deleteGroupImpl(listOf(mockFavorite1))

        // then
        assertEquals(
            1,
            deleted
        )
    }

    @Test
    fun shouldDeleteOnlyOnePodcast() = coroutinesRule.runBlocking {
        // when
        val deleted = dao.deleteGroupPodcastImpl(listOf(mockPodcastFavorite1))

        // then
        assertEquals(
            1,
            deleted
        )
    }

    @Test
    fun testGetTrack() = coroutinesRule.runBlocking {
        // given
        val id = mockFavorite1.songId

        // when
        val item = dao.getTrackById(id)

        // then
        assertNotNull(item)
    }

    @Test
    fun testGetPodcast() = coroutinesRule.runBlocking {
        // given
        val id = mockPodcastFavorite1.podcastId

        // when
        val item = dao.getPodcastById(id)

        // then
        assertNotNull(item)
    }

    @Test
    fun testIsFavorite() = coroutinesRule.runBlocking {
        // given
        val id = mockFavorite1.songId

        // when
        val favorite = dao.isFavorite(id)

        // then
        assertTrue(favorite)
    }

    @Test
    fun testIsNotFavorite() = coroutinesRule.runBlocking {
        // given
        val id = -1L

        // when
        val favorite = dao.isFavorite(id)

        // then
        assertFalse(favorite)
    }

    @Test
    fun testPodcastIsFavorite() = coroutinesRule.runBlocking {
        // given
        val id = mockPodcastFavorite1.podcastId

        // when
        val favorite = dao.isFavoritePodcast(id)

        // then
        assertTrue(favorite)
    }

    @Test
    fun testPodcastIsNotFavorite() = coroutinesRule.runBlocking {
        // given
        val id = -1L

        // when
        val favorite = dao.isFavoritePodcast(id)

        // then
        assertFalse(favorite)
    }

}