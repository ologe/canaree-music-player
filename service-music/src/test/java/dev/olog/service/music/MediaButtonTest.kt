package dev.olog.service.music

import androidx.lifecycle.Lifecycle
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import dev.olog.service.music.EventDispatcher.Event
import dev.olog.test.shared.MainCoroutineRule
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class MediaButtonTest {

    @get:Rule
    var coroutinesMainDispatcherRule = MainCoroutineRule()

    private val eventDispatcher = mock<EventDispatcher>()
    private val lifecycle = mock<Lifecycle>()
    private val mediaButton = MediaButton(lifecycle, eventDispatcher)

    @Test
    fun `test no clicks`() = runBlockingTest {
        testNTimes(0) {
            verifyZeroInteractions(eventDispatcher)
        }
    }

    @Test
    fun `test 1 clicks`() = runBlockingTest {
        testNTimes(1) {
            verify(eventDispatcher).dispatchEvent(Event.PLAY_PAUSE)
        }
    }

    @Test
    fun `test 2 clicks`() = runBlockingTest {
        testNTimes(2) {
            verify(eventDispatcher).dispatchEvent(Event.SKIP_NEXT)
        }
    }

    @Test
    fun `test 3 clicks`() = runBlockingTest {
        testNTimes(3) {
            verify(eventDispatcher).dispatchEvent(Event.SKIP_PREVIOUS)
        }
    }

    @Test
    fun `test too many clicks`() = runBlockingTest {
        testNTimes(MediaButton.MAX_ALLOWED_CLICKS + 1) {
            verifyZeroInteractions(eventDispatcher)
        }
    }

    private suspend fun testNTimes(times: Int, verify: () -> Unit) {
        val latch = CountDownLatch(1)

        repeat(times) {
            mediaButton.onHeatSetHookClick()
            delay(10)
        }

        latch.await(MediaButton.DELAY, TimeUnit.MILLISECONDS)

        verify()
    }

}