package dev.olog.data.playing.queue

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import dev.olog.core.EMPTY
import dev.olog.core.entity.PlayingQueueSong
import dev.olog.core.entity.track.Song
import dev.olog.core.gateway.track.SongGateway
import dev.olog.core.interactor.UpdatePlayingQueueUseCase
import dev.olog.data.PlayingQueueQueries
import dev.olog.data.extensions.QueryList
import dev.olog.data.extensions.mockTransacter
import dev.olog.data.playingQueue.SelectAll
import dev.olog.flow.test.observer.test
import dev.olog.shared.android.permission.Permission
import dev.olog.shared.android.permission.PermissionManager
import dev.olog.test.shared.TestSchedulers
import dev.olog.test.shared.thenReturnList
import dev.olog.testing.emptySelectAll
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PlayingQueueRepositoryTest {

    private val permissionManager = mock<PermissionManager>()
    private val queries = mock<PlayingQueueQueries>()
    private val songGateway = mock<SongGateway>()
    private val sut = PlayingQueueRepository(
        schedulers = TestSchedulers(),
        permissionManager = permissionManager,
        queries = queries,
        songGateway = songGateway,
    )

    @Before
    fun setup() {
        mockTransacter(queries)
    }

    @Test
    fun `test getAll, should return empty when no permission`() {
        whenever(permissionManager.hasPermission(Permission.Storage))
            .thenReturn(false)

        val actual = sut.getAll()
        Assert.assertEquals(emptyList<PlayingQueueSong>(), actual)
    }

    @Test
    fun `test getAll, should return empty when has permission and last queue is empty`() {
        whenever(permissionManager.hasPermission(Permission.Storage))
            .thenReturn(true)
        val queueQuery = QueryList(emptyList<SelectAll>())
        whenever(queries.selectAll()).thenReturn(queueQuery)

        val song1 = Song.EMPTY.copy(id = 1)
        val song2 = Song.EMPTY.copy(id = 2)
        whenever(songGateway.getAll()).thenReturnList(song1, song2)

        val actual = sut.getAll()
        val expected = listOf(
            PlayingQueueSong.EMPTY.copy(song = song1, playOrder = 0),
            PlayingQueueSong.EMPTY.copy(song = song2, playOrder = 1),
        )
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test getAll, should return items when has permission and last queue is not empty`() {
        whenever(permissionManager.hasPermission(Permission.Storage))
            .thenReturn(true)
        val queueQuery = QueryList(
            emptySelectAll().copy(id = 1, play_order = 10),
            emptySelectAll().copy(id = 2, play_order = 20),
        )
        whenever(queries.selectAll()).thenReturn(queueQuery)

        val actual = sut.getAll()
        val expected = listOf(
            PlayingQueueSong.EMPTY.copy(song = Song.EMPTY.copy(id = 1), playOrder = 10),
            PlayingQueueSong.EMPTY.copy(song = Song.EMPTY.copy(id = 2), playOrder = 20),
        )
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `test observeAll`() = runTest {
        whenever(permissionManager.awaitPermission(Permission.Storage)).thenReturn(Unit)

        val queueQuery = QueryList(
            emptySelectAll().copy(id = 1, play_order = 10),
            emptySelectAll().copy(id = 2, play_order = 20),
        )
        whenever(queries.selectAll()).thenReturn(queueQuery)

        val expected = listOf(
            PlayingQueueSong.EMPTY.copy(song = Song.EMPTY.copy(id = 1), playOrder = 10),
            PlayingQueueSong.EMPTY.copy(song = Song.EMPTY.copy(id = 2), playOrder = 20),
        )

        sut.observeAll().test(this) {
            assertValue(expected)
        }

        advanceUntilIdle()

        val inOrder = inOrder(permissionManager, queries)
        inOrder.verify(permissionManager).awaitPermission(Permission.Storage)
        inOrder.verify(queries).selectAll()
    }

    @Test
    fun `test update`() = runTest {
        val request = listOf(
            UpdatePlayingQueueUseCase.Request(1, 10),
            UpdatePlayingQueueUseCase.Request(2, 20),
        )

        sut.update(request)

        val inOrder = inOrder(queries)
        inOrder.verify(queries).transaction(any(), any())
        inOrder.verify(queries).deleteAll()
        inOrder.verify(queries).insert(1, 10)
        inOrder.verify(queries).insert(2, 20)
    }

}