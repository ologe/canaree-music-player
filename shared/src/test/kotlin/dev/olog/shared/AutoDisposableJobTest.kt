package dev.olog.shared

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test


class AutoDisposableJobTest {

    private var sut by autoDisposeJob()

    @Test
    fun `initial value should be null`() {
        assertEquals(null, sut)
    }

    @Test
    fun `test first subscribe`() = runBlockingTest {
        val newJob = launch {
            delay(10_000)
        }

        // when
        sut = newJob

        // then
        assertEquals(true, newJob.isActive)
        advanceUntilIdle()
        assertEquals(true, newJob.isCompleted)
    }

    @Test
    fun `test multiple subscribe`() = runBlockingTest {
        val firstJob = launch {
            delay(10_000)
        }
        val secondJob = launch {
            delay(20_000)
        }
        sut = firstJob

        // when
        sut = secondJob

        // then
        assertEquals(true, firstJob.isCancelled)
        assertEquals(true, secondJob.isActive)
        advanceUntilIdle()
        assertEquals(true, secondJob.isCompleted)
    }

    @Test
    fun `test job cancellation`() = runBlockingTest {
        val firstJob = launch {
            delay(10_000)
        }

        sut = firstJob
        assertEquals(true, firstJob.isActive)

        // when
        sut = null

        // then
        advanceUntilIdle()
        assertEquals(true, firstJob.isCancelled)

    }
}