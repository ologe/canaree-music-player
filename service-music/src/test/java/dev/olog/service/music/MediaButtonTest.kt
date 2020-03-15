package dev.olog.service.music

import androidx.lifecycle.Lifecycle
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import dev.olog.service.music.EventDispatcher.Event
import dev.olog.test.shared.MainCoroutineRule
import dev.olog.test.shared.runBlockingTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class MediaButtonTest {

    @get:Rule
    var coroutinesRule = MainCoroutineRule()

    private val eventDispatcher = mock<EventDispatcher>()
    private val lifecycle = mock<Lifecycle>()
    private val sut = MediaButton(lifecycle, eventDispatcher)

    @Test
    fun `test 1 clicks`() = coroutinesRule.runBlockingTest {
        sut.onHeatSetHookClick()

        it.advanceUntilIdle()

        verify(eventDispatcher).dispatchEvent(Event.PLAY_PAUSE)
    }

    @Test
    fun `test 2 clicks`() = coroutinesRule.runBlockingTest {
        repeat(2) {
            sut.onHeatSetHookClick()
        }

        it.advanceUntilIdle()

        verify(eventDispatcher).dispatchEvent(Event.SKIP_NEXT)
    }

    @Test
    fun `test 3 clicks`() = coroutinesRule.runBlockingTest {
        repeat(3) {
            sut.onHeatSetHookClick()
        }

        it.advanceUntilIdle()

        verify(eventDispatcher).dispatchEvent(Event.SKIP_PREVIOUS)
    }

    @Test
    fun `test too many clicks`() = coroutinesRule.runBlockingTest {
        repeat(MediaButton.MAX_ALLOWED_CLICKS + 1) {
            sut.onHeatSetHookClick()
        }

        it.advanceUntilIdle()

        verifyZeroInteractions(eventDispatcher)
    }

}