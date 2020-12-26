package dev.olog.service.music.queue

import androidx.test.filters.FlakyTest
import dev.olog.core.DateTimeGenerator
import dev.olog.service.music.model.MediaEntity
import dev.olog.service.music.model.PositionInQueue
import dev.olog.service.music.model.SkipType
import dev.olog.service.music.player.InternalPlayerState
import dev.olog.test.shared.LifecycleOwnerRule
import dev.olog.test.shared.MainCoroutineRule
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import kotlin.random.Random
import kotlin.system.measureTimeMillis
import kotlin.time.Duration
import kotlin.time.milliseconds

class EnhancedShuffleTimeComplexityTest {

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
        random = Random
    )

    @Test
    @FlakyTest
    fun test1_000items() {
        executeForXItems(
            itemsAlreadyPlayed = 1_000,
            timeTolerance = 4.milliseconds
        )
    }

    @Test
    @FlakyTest
    fun test10_000items() {
        executeForXItems(
            itemsAlreadyPlayed = 10_000,
            timeTolerance = 40.milliseconds
        )
    }

    @Test
    @FlakyTest
    fun test100_000items() {
        executeForXItems(
            itemsAlreadyPlayed = 100_000,
            timeTolerance = 400.milliseconds
        )
    }

    private fun executeForXItems(itemsAlreadyPlayed: Int, timeTolerance: Duration) {
        prepareAlreadyPlayed(itemsAlreadyPlayed)

        val originalList = (0 until (itemsAlreadyPlayed * 2))
            .map { MediaEntity.EMPTY.copy(id = it.toLong()) }

        val time = measureTimeMillis {
            sut(originalList)
        }

        assertTrue(
            "Executed in $time, but wanted at most $timeTolerance",
            time < timeTolerance.toLongMilliseconds()
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