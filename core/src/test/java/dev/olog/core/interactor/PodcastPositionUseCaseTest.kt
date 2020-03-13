package dev.olog.core.interactor

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dev.olog.core.gateway.podcast.PodcastGateway
import org.junit.Assert.assertEquals
import org.junit.Test

class PodcastPositionUseCaseTest {

    private val gateway = mock<PodcastGateway>()
    private val sut = PodcastPositionUseCase(gateway)

    @Test
    fun testGet() {
        val id = 1L
        val duration = 100L
        val expected = 12345L
        whenever(gateway.getCurrentPosition(id, duration))
            .thenReturn(expected)

        val actual = sut.get(id, duration)

        verify(gateway).getCurrentPosition(id, duration)
        assertEquals(expected, actual)
    }

    @Test
    fun testSet() {
        val id = 1L
        val position = 100L

        sut.set(id, position)

        verify(gateway).saveCurrentPosition(id, position)
    }

}