package dev.olog.core.coroutines

import dev.olog.core.coroutines.autoDisposeJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AutoDisposableJobTest {

    private var sut by autoDisposeJob()

    @Test
    fun `test initial value`() {

        assertNull("should be null", sut)
    }

    @Suppress("UNUSED_VALUE")
    @Test
    fun `test first subscribe`() = runBlockingTest {
        // given
        val newJob = launch {
            delay(10_000)
        }

        // when
        sut = newJob

        // then
        assertTrue("should be active", newJob.isActive)
        advanceTimeBy(Long.MAX_VALUE)
        assertTrue("should be completed", newJob.isCompleted)
    }

    @Test
    fun `test multiple subscribe`() = runBlockingTest {
        // given

        val firstJob = launch {
            delay(10_000)
        }
        val secondJob = launch {
            delay(10_000)
        }
        sut = firstJob

        // when
        sut = secondJob

        // then
        assertTrue("should be cancelled", firstJob.isCancelled)
        assertTrue("should be active", secondJob.isActive)
    }

    @Test
    fun `test job cancellation`() = runBlockingTest {
        // given
        val firstJob = launch {
            delay(10_000)
        }

        sut = firstJob
        assertTrue("should be active", firstJob.isActive)

        // when
        sut = null

        // then
        assertTrue("should be cancelled", firstJob.isCancelled)

    }
}