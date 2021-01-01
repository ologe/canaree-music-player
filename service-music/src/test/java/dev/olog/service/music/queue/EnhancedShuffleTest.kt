package dev.olog.service.music.queue

import dev.olog.domain.DateTimeGenerator
import dev.olog.service.music.model.MediaEntity
import dev.olog.service.music.model.PositionInQueue
import dev.olog.service.music.model.SkipType
import dev.olog.service.music.player.InternalPlayerState
import dev.olog.test.shared.LifecycleOwnerRule
import dev.olog.test.shared.MainCoroutineRule
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert
import org.junit.Rule
import org.junit.jupiter.api.Test
import kotlin.random.Random

class EnhancedShuffleTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @get:Rule
    val lifecycleOwnerRule = LifecycleOwnerRule()

    private val playerState = InternalPlayerState()
    private val dateTimeGenerator = mockk<DateTimeGenerator>()
    private val sut = EnhancedShuffle(
        lifecycleOwner = lifecycleOwnerRule.owner,
        playerState = playerState,
        dateTimeGenerator = dateTimeGenerator,
        random = Random(124)
    )

    @Test
    fun `test empty, should return empty list`() = coroutineRule {
        Assert.assertEquals(
            emptyList<MediaEntity>(),
            sut(listOf())
        )
    }

    @Test
    fun `test 10 tracks, 8 already played, 2 new`() = coroutineRule {
        prepareAlreadyPlayed(8)

        val originalList = (0 until 10)
            .map { MediaEntity.EMPTY.copy(id = it.toLong()) }

        val actual = sut(originalList)

        val expected = listOf(
            // randomly shuffle
            MediaEntity.EMPTY.copy(id = 8),
            MediaEntity.EMPTY.copy(id = 9),
            // first half played tracks shuffled
            MediaEntity.EMPTY.copy(id = 0),
            MediaEntity.EMPTY.copy(id = 3),
            MediaEntity.EMPTY.copy(id = 2),
            MediaEntity.EMPTY.copy(id = 1),
            // last half played tracks in reverse order
            MediaEntity.EMPTY.copy(id = 4),
            MediaEntity.EMPTY.copy(id = 5),
            MediaEntity.EMPTY.copy(id = 6),
            MediaEntity.EMPTY.copy(id = 7),
        )

        // assert last
        Assert.assertEquals(originalList.size, actual.size)

        Assert.assertEquals(
            expected.map { it.id },
            actual.map { it.id }
        )
    }

    @Test
    fun `test 10 tracks, all already played`() = coroutineRule {
        prepareAlreadyPlayed(10)

        val originalList = (0 until 10)
            .map { MediaEntity.EMPTY.copy(id = it.toLong()) }

        val actual = sut(originalList)

        val expected = listOf(
            // no randomly shuffle
//            ---
            // first half played tracks shuffled
            MediaEntity.EMPTY.copy(id = 2),
            MediaEntity.EMPTY.copy(id = 3),
            MediaEntity.EMPTY.copy(id = 1),
            MediaEntity.EMPTY.copy(id = 4),
            MediaEntity.EMPTY.copy(id = 0),
            // last half played tracks in reverse order
            MediaEntity.EMPTY.copy(id = 5),
            MediaEntity.EMPTY.copy(id = 6),
            MediaEntity.EMPTY.copy(id = 7),
            MediaEntity.EMPTY.copy(id = 8),
            MediaEntity.EMPTY.copy(id = 9),
        )

        // assert last
        Assert.assertEquals(originalList.size, actual.size)

        Assert.assertEquals(
            expected.map { it.id },
            actual.map { it.id }
        )
    }

    @Test
    fun `test 10 tracks, all never played`() = coroutineRule {
        val originalList = (0 until 10)
            .map { MediaEntity.EMPTY.copy(id = it.toLong()) }

        val actual = sut(originalList)

        val expected = listOf(
            // randomly shuffle
            MediaEntity.EMPTY.copy(id = 2),
            MediaEntity.EMPTY.copy(id = 8),
            MediaEntity.EMPTY.copy(id = 7),
            MediaEntity.EMPTY.copy(id = 6),
            MediaEntity.EMPTY.copy(id = 0),
            MediaEntity.EMPTY.copy(id = 9),
            MediaEntity.EMPTY.copy(id = 1),
            MediaEntity.EMPTY.copy(id = 5),
            MediaEntity.EMPTY.copy(id = 3),
            MediaEntity.EMPTY.copy(id = 4),
        )

        // assert last
        Assert.assertEquals(originalList.size, actual.size)

        Assert.assertEquals(
            expected.map { it.id },
            actual.map { it.id }
        )
    }

    private fun prepareAlreadyPlayed(times: Int) {
        every { dateTimeGenerator.now() } returnsMany (0 until times).map { it * 100L }

        for (index in 0 until times) {
            playerState.prepare(
                entity = MediaEntity.EMPTY.copy(id = index.toLong()),
                positionInQueue = PositionInQueue.IN_MIDDLE,
                bookmark = 0,
                skipType = SkipType.NONE,
                isPlaying = false
            )
        }
    }

}