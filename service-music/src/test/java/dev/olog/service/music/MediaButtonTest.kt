package dev.olog.service.music

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dev.olog.service.music.EventDispatcher.Event
import dev.olog.test.shared.CoroutinesMainDispatcherRule
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test

class MediaButtonTest {

    @get:Rule
    var coroutinesMainDispatcherRule = CoroutinesMainDispatcherRule()

    private val eventDispatcher = mock<EventDispatcher>()
    private val mediaButton = MediaButton(eventDispatcher)

    @Test
    fun `test no clicks`() {

    }

    @Test
    fun `test 1 clicks`() = runBlocking {
        mediaButton.onHeatSetHookClick()

        verify(eventDispatcher).dispatchEvent(Event.PLAY_PAUSE)
    }

}