package dev.olog.data.repository.track

import android.content.ContentValues
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import dev.olog.domain.entity.sort.SortArranging
import dev.olog.domain.entity.sort.SortEntity
import dev.olog.domain.entity.sort.SortType
import dev.olog.domain.entity.track.Song
import dev.olog.domain.gateway.track.TrackGateway
import dev.olog.domain.prefs.BlacklistPreferences
import dev.olog.domain.prefs.SortPreferences
import dev.olog.data.queries.TrackQueries
import dev.olog.data.test.InMemoryContentProvider
import dev.olog.data.test.InMemoryContentProvider.Companion.AUDIO
import dev.olog.test.shared.MainCoroutineRule
import dev.olog.test.shared.runBlockingTest
import dev.olog.test.shared.schedulers
import kotlinx.coroutines.flow.first
import org.junit.After
import org.junit.Assert.*
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class SongRepositoryIntegrationTest {

    private val blacklistPrefs = mock<BlacklistPreferences>()
    private val sortPrefs = mock<SortPreferences>()

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    lateinit var queries: TrackQueries
    lateinit var sut: TrackGateway

    @After
    fun teardown() {
        val context = InstrumentationRegistry.getInstrumentation().context
        context.contentResolver.delete(InMemoryContentProvider.getContentUri(AUDIO), null, null)
    }

    @Test
    fun testGetAllIsNotPodcast() = coroutineRule.runBlockingTest {
        // given
        whenever(blacklistPrefs.getBlackList()).thenReturn(setOf())
        whenever(sortPrefs.getAllTracksSort()).thenReturn(
            SortEntity(SortType.TITLE, SortArranging.ASCENDING)
        )
        setup(getDefaultTracks())

        // when
        val actual = sut.getAll()

        // then
        assertTrue(actual.none { it.isPodcast })
    }

    @Test
    fun testNoneBlacklisted() = coroutineRule.runBlockingTest {
        // given
        whenever(blacklistPrefs.getBlackList()).thenReturn(setOf())
        whenever(sortPrefs.getAllTracksSort()).thenReturn(
            SortEntity(SortType.TITLE, SortArranging.ASCENDING)
        )
        setup(getDefaultTracks())

        // when
        val actual = sut.getAll()

        // then
        assertEquals(4, actual.size)
    }

    // region blacklist

    @Test
    fun testOneFolderBlacklisted() = coroutineRule.runBlockingTest {
        // given
        whenever(blacklistPrefs.getBlackList()).thenReturn(
            setOf("/storage/emulated/0/rap")
        )
        whenever(sortPrefs.getAllTracksSort()).thenReturn(
            SortEntity(SortType.TITLE, SortArranging.ASCENDING)
        )

        val data = getDefaultTracks().filter { !it.isPodcast }
            .toMutableList()
            .mapItems(
                {
                    it.copy(
                        path = "/storage/emulated/0/rap/track 4.mp3",
                        displayName = "track 4.mp3"
                    )
                },
                {
                    it.copy(
                        path = "/storage/emulated/0/rap/track 3.mp3",
                        displayName = "track 3.mp3"
                    )
                },
                {
                    it.copy(
                        path = "/storage/emulated/0/music/track 6.mp3",
                        displayName = "track 6.mp3",
                        title = "track 6"
                    )
                },
                {
                    it.copy(
                        path = "/storage/emulated/0/reggaeton/track 1.mp3",
                        displayName = "track 1.mp3",
                        title = "track 1"
                    )
                }
            )

        setup(data)

        // when
        val actual = sut.getAll()

        // then
        assertEquals(
            listOf("track 1", "track 6"),
            actual.map { it.title }
        )
    }

    @Test
    fun testMultipleFolderBlacklisted() = coroutineRule.runBlockingTest {
        // given
        whenever(blacklistPrefs.getBlackList()).thenReturn(
            setOf(
                "/storage/emulated/0/rap",
                "/storage/emulated/0/hip hop",
                "/storage/emulated/0/music",
                "/storage/emulated/0/random"
            )
        )
        whenever(sortPrefs.getAllTracksSort()).thenReturn(
            SortEntity(SortType.TITLE, SortArranging.ASCENDING)
        )

        val data = getDefaultTracks().filter { !it.isPodcast }
            .toMutableList()
            .mapItems(
                {
                    it.copy(
                        path = "/storage/emulated/0/rap/track 4.mp3",
                        displayName = "track 4.mp3"
                    )
                },
                {
                    it.copy(
                        path = "/storage/emulated/0/rap/track 3.mp3",
                        displayName = "track 3.mp3"
                    )
                },
                {
                    it.copy(
                        path = "/storage/emulated/0/music/track 6.mp3",
                        displayName = "track 6.mp3",
                        title = "track 6"
                    )
                },
                {
                    it.copy(
                        path = "/storage/emulated/0/reggaeton/track 1.mp3",
                        displayName = "track 1.mp3",
                        title = "track 1"
                    )
                }
            )

        setup(data)

        // when
        val actual = sut.getAll()

        // then
        assertEquals(
            listOf("track 1"),
            actual.map { it.title }
        )
    }

    @Test
    fun testAllFolderBlacklisted() = coroutineRule.runBlockingTest {
        // given
        whenever(blacklistPrefs.getBlackList()).thenReturn(
            setOf(
                "/storage/emulated/0/rap",
                "/storage/emulated/0/hip hop"
            )
        )
        whenever(sortPrefs.getAllTracksSort()).thenReturn(
            SortEntity(SortType.TITLE, SortArranging.ASCENDING)
        )

        val data = getDefaultTracks().filter { !it.isPodcast }
            .toMutableList()
            .mapItems(
                {
                    it.copy(
                        path = "/storage/emulated/0/rap/track 4.mp3",
                        displayName = "track 4.mp3"
                    )
                },
                {
                    it.copy(
                        path = "/storage/emulated/0/rap/track 3.mp3",
                        displayName = "track 3.mp3"
                    )
                },
                {
                    it.copy(
                        path = "/storage/emulated/0/hip hop/track 6.mp3",
                        displayName = "track 6.mp3"
                    )
                },
                {
                    it.copy(
                        path = "/storage/emulated/0/rap/track 1.mp3",
                        displayName = "track 1.mp3"
                    )
                }
            )

        setup(data)

        // when
        val actual = sut.getAll()

        // then
        assertEquals(
            emptyList<String>(),
            actual.map { it.title }
        )
    }

    @Test
    fun testTooManyFolderBlacklistedShouldTakeOnly999() = coroutineRule.runBlockingTest {
        // given
        whenever(blacklistPrefs.getBlackList()).thenReturn(
            setOf("/storage/emulated/0/rap") + (0 until 10000).map { "$it" }.toSet()
        )
        whenever(sortPrefs.getAllTracksSort()).thenReturn(
            SortEntity(SortType.TITLE, SortArranging.ASCENDING)
        )

        val data = getDefaultTracks().filter { !it.isPodcast }
            .toMutableList()
            .mapItems(
                {
                    it.copy(
                        path = "/storage/emulated/0/rap/track 4.mp3",
                        displayName = "track 4.mp3"
                    )
                },
                {
                    it.copy(
                        path = "/storage/emulated/0/rap/track 3.mp3",
                        displayName = "track 3.mp3"
                    )
                },
                {
                    it.copy(
                        path = "/storage/emulated/0/rap/track 6.mp3",
                        displayName = "track 6.mp3"
                    )
                },
                {
                    it.copy(
                        path = "/storage/emulated/0/rap/track 1.mp3",
                        displayName = "track 1.mp3"
                    )
                }
            )

        setup(data)

        // when
        val actual = sut.getAll()

        // then
        assertEquals(
            emptyList<String>(),
            actual.map { it.title }
        )
    }

    // endregion

    // region sort

    @Test
    fun testGetAllSortByTitleAsc() = coroutineRule.runBlockingTest {
        // given
        whenever(blacklistPrefs.getBlackList()).thenReturn(setOf())
        whenever(sortPrefs.getAllTracksSort()).thenReturn(
            SortEntity(SortType.TITLE, SortArranging.ASCENDING)
        )

        val data = getDefaultTracks().filter { !it.isPodcast }
            .toMutableList()
            .mapItems(
                { it.copy(title = "Z last") },
                { it.copy(title = "beyoncé") },
                { it.copy(title = "Beyonce") },
                { it.copy(title = "G") }
            )

        setup(data)

        // when
        val items = sut.getAll()

        // then
        assertEquals(
            listOf("Beyonce", "beyoncé", "G", "Z last"),
            items.map { it.title }
        )
    }

    @Test
    fun testGetAllSortByTitleDesc() = coroutineRule.runBlockingTest {
        // given
        whenever(blacklistPrefs.getBlackList()).thenReturn(setOf())
        whenever(sortPrefs.getAllTracksSort()).thenReturn(
            SortEntity(SortType.TITLE, SortArranging.DESCENDING)
        )

        val data = getDefaultTracks().filter { !it.isPodcast }
            .toMutableList()
            .mapItems(
                { it.copy(title = "Z last") },
                { it.copy(title = "beyoncé") },
                { it.copy(title = "Beyonce") },
                { it.copy(title = "G") }
            )

        setup(data)

        // when
        val items = sut.getAll()

        // then
        assertEquals(
            listOf("Z last", "G", "beyoncé", "Beyonce"),
            items.map { it.title }
        )
    }

    @Test
    fun testGetAllSortByArtistAsc() = coroutineRule.runBlockingTest {
        // given
        whenever(blacklistPrefs.getBlackList()).thenReturn(setOf())
        whenever(sortPrefs.getAllTracksSort()).thenReturn(
            SortEntity(SortType.ARTIST, SortArranging.ASCENDING)
        )

        val data = getDefaultTracks().filter { !it.isPodcast }
            .toMutableList()
            .mapItems(
                { it.copy(artist = "Z last") },
                { it.copy(artist = "beyoncé") },
                { it.copy(artist = "Beyonce") },
                { it.copy(artist = "G") }
            )

        setup(data)

        // when
        val items = sut.getAll()

        // then
        assertEquals(
            listOf("Beyonce", "beyoncé", "G", "Z last"),
            items.map { it.artist }
        )
    }

    @Test
    fun testGetAllSortByArtistDesc() = coroutineRule.runBlockingTest {
        // given
        whenever(blacklistPrefs.getBlackList()).thenReturn(setOf())
        whenever(sortPrefs.getAllTracksSort()).thenReturn(
            SortEntity(SortType.ARTIST, SortArranging.DESCENDING)
        )

        val data = getDefaultTracks().filter { !it.isPodcast }
            .toMutableList()
            .mapItems(
                { it.copy(artist = "Z last") },
                { it.copy(artist = "beyoncé") },
                { it.copy(artist = "Beyonce") },
                { it.copy(artist = "G") }
            )

        setup(data)

        // when
        val items = sut.getAll()

        // then
        assertEquals(
            listOf("Z last", "G", "beyoncé", "Beyonce"),
            items.map { it.artist }
        )
    }

    @Test
    fun testGetAllSortByAlbumAsc() = coroutineRule.runBlockingTest {
        // given
        whenever(blacklistPrefs.getBlackList()).thenReturn(setOf())
        whenever(sortPrefs.getAllTracksSort()).thenReturn(
            SortEntity(SortType.ALBUM, SortArranging.ASCENDING)
        )

        val data = getDefaultTracks().filter { !it.isPodcast }
            .toMutableList()
            .mapItems(
                { it.copy(album = "Z last") },
                { it.copy(album = "beyoncé") },
                { it.copy(album = "Beyonce") },
                { it.copy(album = "G") }
            )

        setup(data)

        // when
        val items = sut.getAll()

        // then
        assertEquals(
            listOf("Beyonce", "beyoncé", "G", "Z last"),
            items.map { it.album }
        )
    }

    @Test
    fun testGetAllSortByAlbumDesc() = coroutineRule.runBlockingTest {
        // given
        whenever(blacklistPrefs.getBlackList()).thenReturn(setOf())
        whenever(sortPrefs.getAllTracksSort()).thenReturn(
            SortEntity(SortType.ALBUM, SortArranging.DESCENDING)
        )

        val data = getDefaultTracks().filter { !it.isPodcast }
            .toMutableList()
            .mapItems(
                { it.copy(album = "Z last") },
                { it.copy(album = "beyoncé") },
                { it.copy(album = "Beyonce") },
                { it.copy(album = "G") }
            )

        setup(data)

        // when
        val items = sut.getAll()

        // then
        assertEquals(
            listOf("Z last", "G", "beyoncé", "Beyonce"),
            items.map { it.album }
        )
    }

    @Test
    fun testGetAllSortByAlbumArtistAsc() = coroutineRule.runBlockingTest {
        // given
        whenever(blacklistPrefs.getBlackList()).thenReturn(setOf())
        whenever(sortPrefs.getAllTracksSort()).thenReturn(
            SortEntity(SortType.ALBUM_ARTIST, SortArranging.ASCENDING)
        )

        val data = getDefaultTracks().filter { !it.isPodcast }
            .toMutableList()
            .mapItems(
                { it.copy(albumArtist = "Z last") },
                { it.copy(albumArtist = "beyoncé") },
                { it.copy(albumArtist = "Beyonce") },
                { it.copy(albumArtist = "G") }
            )

        setup(data)

        // when
        val items = sut.getAll()

        // then
        assertEquals(
            listOf("Beyonce", "beyoncé", "G", "Z last"),
            items.map { it.albumArtist }
        )
    }

    @Test
    fun testGetAllSortByAlbumArtistDesc() = coroutineRule.runBlockingTest {
        // given
        whenever(blacklistPrefs.getBlackList()).thenReturn(setOf())
        whenever(sortPrefs.getAllTracksSort()).thenReturn(
            SortEntity(SortType.ALBUM_ARTIST, SortArranging.DESCENDING)
        )

        val data = getDefaultTracks().filter { !it.isPodcast }
            .toMutableList()
            .mapItems(
                { it.copy(albumArtist = "Z last") },
                { it.copy(albumArtist = "beyoncé") },
                { it.copy(albumArtist = "Beyonce") },
                { it.copy(albumArtist = "G") }
            )

        setup(data)

        // when
        val items = sut.getAll()

        // then
        assertEquals(
            listOf("Z last", "G", "beyoncé", "Beyonce"),
            items.map { it.albumArtist }
        )
    }

    @Test
    fun testGetAllSortByDurationAsc() = coroutineRule.runBlockingTest {
        // given
        whenever(blacklistPrefs.getBlackList()).thenReturn(setOf())
        whenever(sortPrefs.getAllTracksSort()).thenReturn(
            SortEntity(SortType.DURATION, SortArranging.ASCENDING)
        )

        val data = getDefaultTracks().filter { !it.isPodcast }
            .toMutableList()
            .mapItems(
                { it.copy(duration = 1000) },
                { it.copy(duration = 2000) },
                { it.copy(duration = 500) },
                { it.copy(duration = 550) }
            )

        setup(data)

        // when
        val items = sut.getAll()

        // then
        assertEquals(
            listOf(500L, 550L, 1000L, 2000L),
            items.map { it.duration }
        )
    }

    @Test
    fun testGetAllSortByDurationDesc() = coroutineRule.runBlockingTest {
        // given
        whenever(blacklistPrefs.getBlackList()).thenReturn(setOf())
        whenever(sortPrefs.getAllTracksSort()).thenReturn(
            SortEntity(SortType.DURATION, SortArranging.DESCENDING)
        )

        val data = getDefaultTracks().filter { !it.isPodcast }
            .toMutableList()
            .mapItems(
                { it.copy(duration = 1000) },
                { it.copy(duration = 2000) },
                { it.copy(duration = 500) },
                { it.copy(duration = 550) }
            )

        setup(data)

        // when
        val items = sut.getAll()

        // then
        assertEquals(
            listOf(2000L, 1000L, 550L, 500L),
            items.map { it.duration }
        )
    }

    @Test
    fun testGetAllSortByRecentlyAddedAsc() = coroutineRule.runBlockingTest {
        // given
        whenever(blacklistPrefs.getBlackList()).thenReturn(setOf())
        whenever(sortPrefs.getAllTracksSort()).thenReturn(
            SortEntity(SortType.RECENTLY_ADDED, SortArranging.ASCENDING)
        )

        val data = getDefaultTracks().filter { !it.isPodcast }
            .toMutableList()
            .mapItems(
                { it.copy(dateAdded = 1000) },
                { it.copy(dateAdded = 2000) },
                { it.copy(dateAdded = 500) },
                { it.copy(dateAdded = 550) }
            )

        setup(data)

        // when
        val items = sut.getAll()

        // then
        assertEquals(
            listOf(2000L, 1000L, 550L, 500L),
            items.map { it.dateAdded }
        )
    }

    @Test
    fun testGetAllSortByRecentlyAddedDesc() = coroutineRule.runBlockingTest {
        // given
        whenever(blacklistPrefs.getBlackList()).thenReturn(setOf())
        whenever(sortPrefs.getAllTracksSort()).thenReturn(
            SortEntity(SortType.RECENTLY_ADDED, SortArranging.DESCENDING)
        )

        val data = getDefaultTracks().filter { !it.isPodcast }
            .toMutableList()
            .mapItems(
                { it.copy(dateAdded = 1000) },
                { it.copy(dateAdded = 2000) },
                { it.copy(dateAdded = 500) },
                { it.copy(dateAdded = 550) }
            )

        setup(data)

        // when
        val items = sut.getAll()

        // then
        assertEquals(
            listOf(500L, 550L, 1000L, 2000L),
            items.map { it.dateAdded }
        )
    }

    @Test(expected = RuntimeException::class)
    @Ignore(value = "not catching exception")
    fun testGetAllSortByTrackNumber() = coroutineRule.runBlockingTest {
        // given
        whenever(blacklistPrefs.getBlackList()).thenReturn(setOf())
        whenever(sortPrefs.getAllTracksSort()).thenReturn(
            SortEntity(SortType.TRACK_NUMBER, SortArranging.ASCENDING)
        )

        val data = getDefaultTracks().filter { !it.isPodcast }

        setup(data)

        // when
        sut.getAll()
    }

    @Test(expected = RuntimeException::class)
    @Ignore(value = "not catching exception")
    fun testGetAllSortByCustom() {
        coroutineRule.runBlockingTest {
            // given
            whenever(blacklistPrefs.getBlackList()).thenReturn(setOf())
            whenever(sortPrefs.getAllTracksSort()).thenReturn(
                SortEntity(SortType.CUSTOM, SortArranging.ASCENDING)
            )

            val data = getDefaultTracks().filter { !it.isPodcast }

            setup(data)
        }

        // when
        sut.getAll()
    }

    // endregion

    @Test
    fun testGetByParam() = coroutineRule.runBlockingTest {
        // given
        whenever(blacklistPrefs.getBlackList()).thenReturn(setOf())
        whenever(sortPrefs.getAllTracksSort()).thenReturn(
            SortEntity(SortType.TITLE, SortArranging.ASCENDING)
        )

        val data = getDefaultTracks().filter { !it.isPodcast }
            .toMutableList()
            .mapItems(
                { it.copy(id = 1) },
                { it.copy(id = 2) },
                { it.copy(id = 3) },
                { it.copy(id = 4) }
            )

        setup(data)

        // when
        val actual = sut.getByParam(1)!!

        // then
        assertEquals(
            1,
            actual.id
        )
    }

    @Test
    fun testGetByParamFail() = coroutineRule.runBlockingTest {
        // given
        whenever(blacklistPrefs.getBlackList()).thenReturn(setOf())
        whenever(sortPrefs.getAllTracksSort()).thenReturn(
            SortEntity(SortType.TITLE, SortArranging.ASCENDING)
        )

        val data = getDefaultTracks().filter { !it.isPodcast }
            .toMutableList()
            .mapItems(
                { it.copy(id = 1) },
                { it.copy(id = 2) },
                { it.copy(id = 3) },
                { it.copy(id = 4) }
            )

        setup(data)

        // when
        val actual = sut.getByParam(0)

        // then
        assertNull(actual)
    }

    @Test
    fun testGetByParamBlacklisted() = coroutineRule.runBlockingTest {
        // given
        whenever(blacklistPrefs.getBlackList()).thenReturn(setOf("/storage/0"))
        whenever(sortPrefs.getAllTracksSort()).thenReturn(
            SortEntity(SortType.TITLE, SortArranging.ASCENDING)
        )

        val data = getDefaultTracks().filter { !it.isPodcast }
            .toMutableList()
            .mapItems(
                { it.copy(id = 1, path = "/storage/0/item 1", displayName = "item 1") },
                { it.copy(id = 2, path = "/storage/0/item 2", displayName = "item 2") },
                { it.copy(id = 3, path = "/storage/0/item 3", displayName = "item 3") },
                { it.copy(id = 4, path = "/storage/0/item 4", displayName = "item 4") }
            )

        setup(data)

        // when
        val actual = sut.getByParam(1)

        // then
        assertNull(actual)
    }

    @Test
    fun testObserveByParamBlacklisted() = coroutineRule.runBlockingTest {
        // given
        whenever(blacklistPrefs.getBlackList()).thenReturn(setOf())
        whenever(sortPrefs.getAllTracksSort()).thenReturn(
            SortEntity(SortType.TITLE, SortArranging.ASCENDING)
        )

        val data = getDefaultTracks().filter { !it.isPodcast }
            .toMutableList()
            .mapItems(
                { it.copy(id = 1) },
                { it.copy(id = 2) },
                { it.copy(id = 3) },
                { it.copy(id = 4) }
            )

        setup(data)

        // when
        val actual = sut.observeByParam(1).first()!!

        // then
        assertEquals(
            1,
            actual.id
        )
    }

    @Test
    fun testDeleteSingle() = coroutineRule.runBlockingTest {
        // given
        whenever(blacklistPrefs.getBlackList()).thenReturn(setOf())
        whenever(sortPrefs.getAllTracksSort()).thenReturn(
            SortEntity(SortType.TITLE, SortArranging.ASCENDING)
        )

        val data = getDefaultTracks().filter { !it.isPodcast }
            .toMutableList()
            .mapItems(
                { it.copy(id = 1, path = "/storage/0/track 1") },
                { it.copy(id = 2, path = "/storage/0/track 2") },
                { it.copy(id = 3, path = "/storage/0/track 3") },
                { it.copy(id = 4, path = "/storage/0/track 4") }
            )

        setup(data)

        // when
        sut.deleteSingle(1)

        // then
        assertEquals(
            data.drop(1),
            sut.getAll()
        )
    }

    @Test
    fun testDeleteSingleFail() = coroutineRule.runBlockingTest {
        // given
        whenever(blacklistPrefs.getBlackList()).thenReturn(setOf())
        whenever(sortPrefs.getAllTracksSort()).thenReturn(
            SortEntity(SortType.TITLE, SortArranging.ASCENDING)
        )

        val data = getDefaultTracks().filter { !it.isPodcast }
            .toMutableList()
            .mapItems(
                { it.copy(id = 1, path = "/storage/0/track 1") },
                { it.copy(id = 2, path = "/storage/0/track 2") },
                { it.copy(id = 3, path = "/storage/0/track 3") },
                { it.copy(id = 4, path = "/storage/0/track 4") }
            )

        setup(data)

        // when
        sut.deleteSingle(0)

        // then
        assertEquals(
            data.map { it.id }.sorted(),
            sut.getAll().map { it.id }.sorted()
        )
    }

    @Test
    fun testDeleteGroup() = coroutineRule.runBlockingTest {
        // given
        whenever(blacklistPrefs.getBlackList()).thenReturn(setOf())
        whenever(sortPrefs.getAllTracksSort()).thenReturn(
            SortEntity(SortType.TITLE, SortArranging.ASCENDING)
        )

        val data = getDefaultTracks().filter { !it.isPodcast }
            .toMutableList()
            .mapItems(
                { it.copy(id = 1, path = "/storage/0/track 1") },
                { it.copy(id = 2, path = "/storage/0/track 2") },
                { it.copy(id = 3, path = "/storage/0/track 3") },
                { it.copy(id = 4, path = "/storage/0/track 4") }
            )

        setup(data)

        // when
        sut.deleteGroup(listOf(3L, 4L))

        // then
        assertEquals(
            data.dropLast(2).map { it.id }.sorted(),
            sut.getAll().map { it.id }.sorted()
        )
    }

    @Test
    fun testGetByAlbumId() = coroutineRule.runBlockingTest {
        // given
        whenever(blacklistPrefs.getBlackList()).thenReturn(setOf())
        whenever(sortPrefs.getAllTracksSort()).thenReturn(
            SortEntity(SortType.TITLE, SortArranging.ASCENDING)
        )

        val data = getDefaultTracks().filter { !it.isPodcast }
            .toMutableList()
            .mapItems(
                { it.copy(id = 1, albumId = 1) },
                { it.copy(id = 2, albumId = 1) },
                { it.copy(id = 3, albumId = 2) },
                { it.copy(id = 4, albumId = 3) }
            )

        setup(data)

        // when
        val item = sut.getByAlbumId(1)!!

        // then
        assertEquals(
            1L,
            item.albumId
        )
    }

    @Test
    fun testGetByAlbumIdFail() = coroutineRule.runBlockingTest {
        // given
        whenever(blacklistPrefs.getBlackList()).thenReturn(setOf())
        whenever(sortPrefs.getAllTracksSort()).thenReturn(
            SortEntity(SortType.TITLE, SortArranging.ASCENDING)
        )

        val data = getDefaultTracks().filter { !it.isPodcast }
            .toMutableList()
            .mapItems(
                { it.copy(id = 1, albumId = 1) },
                { it.copy(id = 2, albumId = 1) },
                { it.copy(id = 3, albumId = 2) },
                { it.copy(id = 4, albumId = 3) }
            )

        setup(data)

        // when
        val item = sut.getByAlbumId(0)

        // then
        assertNull(item)
    }

    private fun MutableList<Song>.mapItems(
        map1: (Song) -> Song,
        map2: (Song) -> Song,
        map3: (Song) -> Song,
        map4: (Song) -> Song
    ): MutableList<Song> {
        set(0, map1(get(0)))
        set(1, map2(get(1)))
        set(2, map3(get(2)))
        set(3, map4(get(3)))
        return this
    }

    private fun setup(tracks: List<Song>) {
        val context = InstrumentationRegistry.getInstrumentation().context

        queries = TrackQueries(
            context.contentResolver,
            blacklistPrefs,
            sortPrefs,
            false,
            InMemoryContentProvider.getContentUri(AUDIO)
        )

        context.contentResolver.bulkInsert(
            InMemoryContentProvider.getContentUri(AUDIO),
            tracks.map { it.toContentValues() }.toTypedArray()
        )

        sut = TrackRepository(context, coroutineRule.schedulers, queries)
    }

    private fun getDefaultTracks(): List<Song> {
        val context = InstrumentationRegistry.getInstrumentation().context

        val json = context.assets.open("mock/track_and_podcast.json")
            .bufferedReader()
            .use { it.readText() }

        val gson = Gson()
        val collectionType = object : TypeToken<MutableList<Song>>() {}.type
        return gson.fromJson<MutableList<Song>>(json, collectionType)
    }

    private fun Song.toContentValues(): ContentValues {
        return ContentValues().apply {
            put("_id", id)
            put("artist_id", artistId)
            put("album_id", albumId)
            put("title", title)
            put("artist", artist)
            put("album", album)
            put("album_artist", albumArtist)
            put("duration", duration)
            put("date_added", dateAdded)
            put("date_modified", dateModified)
            put("_data", path)
            put("track", trackColumn)
            put("is_podcast", if (isPodcast) 1 else 0)
            put("_display_name", displayName)
        }
    }

}